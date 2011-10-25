package jadex.base.service.message.transport.btmtp;

import jadex.android.bluetooth.JadexBluetoothActivity;
import jadex.android.bluetooth.exceptions.ActivityIsNotJadexBluetoothActivityException;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.message.DataPacket;
import jadex.android.bluetooth.service.ConnectionService;
import jadex.android.bluetooth.service.IBTP2PMessageCallback;
import jadex.android.bluetooth.service.IConnectionServiceConnection;
import jadex.android.bluetooth.util.Helper;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.base.service.message.transport.tcpmtp.TCPTransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.collection.SCollection;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * The tcp transport for sending messages over tcp/ip connections. Initiates one
 * receiving tcp/ip port under the specified settings and opens outgoing
 * connections for all remote platforms on demand.
 * 
 * For the receiving side a separate listener thread is necessary as it must be
 * continuously listened for incoming transmission requests.
 */
public class BTTransport implements ITransport {
	// -------- constants --------

	/** The schema name. */
	public final static String SCHEMA = "bt-mtp://";

	/** Constant for asynchronous setting. */
	public final static String ASYNCHRONOUS = "asynchronous";

	/** How long to keep output connections alive (5 min). */
	protected final static int MAX_KEEPALIVE = 300000;

	/** The prolog size. */
	protected final static int PROLOG_SIZE = 4;

	/** 2MB as message buffer */
	protected final static int BUFFER_SIZE = 1024 * 1024 * 2;

	/** Maximum number of outgoing connections */
	protected final static int MAX_CONNECTIONS = 20;

	/** Default port. */
	protected final static int DEFAULT_PORT = 9876;

	// -------- attributes --------

	/** The platform. */
	protected IServiceProvider container;

	/** The addresses. */
	protected String[] addresses;

	/**
	 * Should be received asynchronously? One thread for receiving is
	 * unavoidable. Async defines if the receival should be done on a new thread
	 * always or on the one receiver thread.
	 */
	protected boolean async;

	/** The codec factory. */
	protected CodecFactory codecfac;

	/** The logger. */
	protected Logger logger;

	/** The library service. */
	protected ILibraryService libservice;

	protected IConnectionServiceConnection binder;

	public IBTP2PMessageCallback msgCallback = new IBTP2PMessageCallback.Stub() {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void messageReceived(final byte[] data) throws RemoteException {
			final Future fut = new Future();
			SServiceProvider.getService(container, IThreadPoolService.class,
					RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
					new DelegationResultListener(fut) {
						public void customResultAvailable(Object result) {
							fut.setResult(null);
							final IThreadPoolService tp = (IThreadPoolService) result;
							tp.execute(new Runnable() {
								public void run() {
									BTTransport.this
											.deliverMessage(BTTransport.this
													.decodeMessage(data));
								}
							});
						}
					});
		}
	};

	private BTServiceConnection sc;

	protected ClassLoader classLoader;

	// -------- constructors --------

	/**
	 * Init the transport.
	 * 
	 * @param platform
	 *            The platform.
	 * @param settings
	 *            The settings.
	 */
	public BTTransport(final IServiceProvider container) {
		this(container, true);
	}

	/**
	 * Init the transport.
	 * 
	 * @param platform
	 *            The platform.
	 * @param settings
	 *            The settings.
	 */
	public BTTransport(final IServiceProvider container, final boolean async) {
		this.logger = Logger.getLogger("BTTransport" + this);
		this.container = container;
		this.async = async;
	}

	/**
	 * Start the transport.
	 */
	public IFuture<?> start() {
		final Future<?> ret = new Future();
		try {
			Context context = JadexBluetoothActivity.application_context;
			if (context != null) {
				Intent intent = new Intent(context, ConnectionService.class);
				Log.d(Helper.LOG_TAG, "(BTTransport) Trying to bind BT Service...");
				sc = new BTServiceConnection(ret);
				context.bindService(intent, sc, Activity.BIND_AUTO_CREATE);
			} else {
				throw new ActivityIsNotJadexBluetoothActivityException();
			}

		} catch (Exception e) {
			// e.printStackTrace();
			ret.setException(new RuntimeException(
					"(BTTransport) Transport initialization error: " + e.getMessage()));
			// throw new
			// RuntimeException("Transport initialization error: "+e.getMessage());
		}
		return ret;
	}

	/**
	 * Perform cleanup operations (if any).
	 */
	public IFuture shutdown() {
		Context context = JadexBluetoothActivity.application_context;
		if (binder != null && context != null) {
			try {
				Log.d(Helper.LOG_TAG, "(BTTransport) Stopping autoconnect...");
				binder.stopAutoConnect();
				binder.stopBTServer();
			} catch (RemoteException e) {
				e.printStackTrace();
			} finally {
				binder = null;
				Log.d(Helper.LOG_TAG, "(BTTransport) Unbinding Service...");
				context.unbindService(sc);
			}
		}
		return new Future(null);
	}

	// -------- methods --------

	/**
	 * Send a message.
	 * 
	 * @param message
	 *            The message to send. (todo: On which thread this should be
	 *            done?)
	 */
	public IFuture sendMessage(Map msg, String type,
			IComponentIdentifier[] receivers, byte[] codecids) {

		// Fetch all addresses
		Set addresses = new LinkedHashSet();
		for (int i = 0; i < receivers.length; i++) {
			String[] raddrs = receivers[i].getAddresses();
			for (int j = 0; j < raddrs.length; j++) {
				addresses.add(raddrs[j]);
			}
		}

		// Iterate over all different addresses and try to send
		// to missing and appropriate receivers
		String[] addrs = (String[]) addresses.toArray(new String[addresses
				.size()]);

		boolean delivered = false;
		BluetoothMessage bluetoothMessage;
		for (int i = 0; !delivered && i < addrs.length; i++) {
			bluetoothMessage = new BluetoothMessage(addrs[i], BTTransport.this.encodeMessage(
					new MessageEnvelope(msg, Arrays.asList(receivers), type),
					codecids), DataPacket.TYPE_DATA);
			try {
				binder.sendMessage(bluetoothMessage);
				delivered = true;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return delivered ? IFuture.DONE : new Future(new RuntimeException(
		"Could not deliver message"));

	}

	private byte[] encodeMessage(MessageEnvelope msg, byte[] codecids) {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		if (codecids == null || codecids.length == 0)
			codecids = codecfac.getDefaultCodecIds();

		Object enc_msg = msg;
		for (int i = 0; i < codecids.length; i++) {
			ICodec codec = codecfac.getCodec(codecids[i]);
			enc_msg = codec.encode(enc_msg, classLoader);
		}
		byte[] res = (byte[]) enc_msg;

		int dynlen = BTTransport.PROLOG_SIZE + 1 + codecids.length;
		int size = res.length + dynlen;
		// System.out.println("len: "+size);
		try {
			os.write((byte) codecids.length);
				os.write(codecids);
			os.write(SUtil.intToBytes(size));
			os.write(res);
			os.flush();
		} catch (IOException e) {
			Log.e(Helper.LOG_TAG, e.getMessage());
		}

		return os.toByteArray();
	}

	/**
	 * Returns the prefix of this transport
	 * 
	 * @return Transport prefix.
	 */
	public String getServiceSchema() {
		return SCHEMA;
	}

	/**
	 * Get the adresses of this transport.
	 * 
	 * @return An array of strings representing the addresses of this message
	 *         transport mechanism.
	 */
	public String[] getAddresses() {
		return addresses;
	}

	// -------- helper methods --------

	/**
	 * Get the address of this transport.
	 * 
	 * @param hostname
	 *            The hostname.
	 * @return <scheme>:<hostname>
	 */
	protected String getAddress(String hostname) {
		return getServiceSchema() + hostname;
	}

	protected MessageEnvelope decodeMessage(byte[] data) {
		MessageEnvelope ret = null;

		// Calculate message size by reading the first 4 bytes
		// Read here is always a blocking call.
		int msg_size;
		byte[] codec_ids = new byte[(int) data[0] & 0xFF];
		int pos = 1;

		for (int i = 0; i < codec_ids.length; i++) {
			codec_ids[i] = data[i + pos];
		}

		msg_size = SUtil.bytesToInt(new byte[] { data[pos + 1], data[pos + 2],
				data[pos + 3], data[pos + 4] });

		pos += 4;

		// readByte() << 24 | readByte() << 16 | readByte() << 8 | readByte();
		// System.out.println("reclen: "+msg_size);
		msg_size = msg_size - BTTransport.PROLOG_SIZE - codec_ids.length - 1; // Remove
																				// prolog.
		if (msg_size > 0) {
			byte[] rawMsg = Arrays.copyOfRange(data, pos, data.length - 1);

			Object tmp = rawMsg;
			for (int i = codec_ids.length - 1; i > -1; i--) {
				ICodec dec = codecfac.getCodec(codec_ids[i]);
				tmp = dec.decode((byte[]) tmp, classLoader);
			}
			ret = (MessageEnvelope) tmp;
		}

		return ret;
	}

	/**
	 * Deliver messages to local message service for disptaching to the
	 * components.
	 * 
	 * @param con
	 *            The connection.
	 */
	protected IFuture deliverMessage(final MessageEnvelope msg) {
		final Future ret = new Future();
		SServiceProvider.getService(container, IMessageService.class,
				RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
				new DefaultResultListener<IMessageService>() {
					public void resultAvailable(IMessageService ms) {
						ms.deliverMessage(msg.getMessage(), msg.getTypeName(),
								msg.getReceivers());
						ret.setResult(null);
					}
				});

		return ret;
	}

	class BTServiceConnection implements ServiceConnection {

		private Future fut;

		public BTServiceConnection(Future fut) {
			this.fut = fut;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			binder = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			binder = IConnectionServiceConnection.Stub.asInterface(arg1);

			Log.d(Helper.LOG_TAG,
					"(BTTransport) Service bound! Retrieving Classloader...");

			SServiceProvider.getService(container, ILibraryService.class,
					RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
					new DelegationResultListener<ILibraryService>(
							new Future<ILibraryService>()) {
						public void customResultAvailable(ILibraryService result) {
							libservice = (ILibraryService) result;
							libservice.getClassLoader(null).addResultListener(
									new DefaultResultListener<ClassLoader>() {

										@Override
										public void resultAvailable(
												ClassLoader result) {
											BTTransport.this.classLoader = result;

											Log.d(Helper.LOG_TAG,
													"(BTTransport) Classloader set. Starting Autoconnect...");

											try {
												addresses = new String[] { getAddress(binder
														.getBTAddress()) };
												binder.registerMessageCallback(msgCallback);
												binder.startAutoConnect();
												fut.setResult(true);
											} catch (RemoteException e) {
												e.printStackTrace();
											}
										}
									});
						}
					});

			SServiceProvider.getService(container, IMessageService.class,
					RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
					new DelegationResultListener<IMessageService>(
							new Future<IMessageService>()) {
						@Override
						public void customResultAvailable(IMessageService ms) {
							BTTransport.this.codecfac = (CodecFactory) ms
									.getCodecFactory();
							Log.d(Helper.LOG_TAG,
									"(BTTransport) CodecFactory set.");
						}
					});
		}
	}
}

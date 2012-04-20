package jadex.bridge.service.types.chat;

import java.io.File;
import java.util.UUID;

import jadex.bridge.IComponentIdentifier;

/**
 *  Information about a requested, ongoing or completed file transfer.
 */
public class TransferInfo
{
	//-------- constants --------
	
	/** State waiting for acceptance. */
	public static final String STATE_WAITING = "Waiting";
	
	/** State when transfer was rejected by receiver. */
	public static final String STATE_REJECTED = "Rejected";
	
	/** State when transfer is in progress. */
	public static final String STATE_TRANSFERRING = "Transferring";

	/** State when transfer was successful. */
	public static final String STATE_COMPLETED = "Completed";
	
	/** State when transfer was aborted by sending or receiving user. */
	public static final String STATE_ABORTED = "Aborted";
	
	/** State when transfer was stopped due to e.g. network error. */
	public static final String STATE_ERROR = "Error";
	
	//-------- attributes --------
	
	/** True for download, false for upload. */
	protected boolean	download;
	
	/** The id. */
	protected String	id;
	
	/** The local file. */
	protected String	file;
	
	/** The ID of the component at the other side of the transfer (i.e. sender for downloads, receiver for uploads). */
	protected IComponentIdentifier other;
	
	/** The size. */
	protected long size;
	
	/** The state. */
	protected String state;
	
	/** The done size. */
	protected long done;
	
	/** The upload/download speed calculated as dynamic moving average (bytes/sec). */
	protected double	speed;
	
	/** The time (millis) of the last update (for calculating speed). */
	protected long	lastupdate;
	
	/** The done size of the last update (for calculating speed). */
	protected long	lastdone;
	
	//-------- constructors --------
	
	/**
	 *  Create a new file transfer info.
	 */
	public TransferInfo()
	{
		// Bean constructor.
	}
	
	/**
	 *  Create a new file transfer info.
	 */
	public TransferInfo(boolean download, String id, String file, IComponentIdentifier other, long size)
	{
		this.download	= download;
		this.id = id!=null ? id : UUID.randomUUID().toString();
		this.file = file;
		this.other = other;
		this.size = size;
	}
	
	//-------- accessors --------

	/**
	 *  Get the file.
	 *  @return the file.
	 */
	public String getFile()
	{
		return file;
	}

	/**
	 *  Set the file.
	 *  @param file The file to set.
	 */
	public void setFile(String file)
	{
		this.file = file;
	}

	/**
	 *  Get the opposite component.
	 *  @return the CID.
	 */
	public IComponentIdentifier getOther()
	{
		return other;
	}

	/**
	 *  Set the opposite component.
	 *  @param other The CID to set.
	 */
	public void setOther(IComponentIdentifier other)
	{
		this.other = other;
	}

	/**
	 *  Get the size.
	 *  @return the size.
	 */
	public long getSize()
	{
		return size;
	}

	/**
	 *  Set the size.
	 *  @param size The size to set.
	 */
	public void setSize(long size)
	{
		this.size = size;
	}

	/**
	 *  Get the done.
	 *  @return the done.
	 */
	public long getDone()
	{
		return done;
	}

	/**
	 *  Set the done.
	 *  @param done The done to set.
	 */
	public void setDone(long done)
	{
		this.done = done;
		
		// Calculate speed every second, but only start timer after first update received.
		long	update	= System.currentTimeMillis();
		if(lastupdate==0)
		{
			lastupdate	= update;
			lastdone	= done;
		}
		else if(update-this.lastupdate>=1000)
		{
			long	dbytes	= done - lastdone;
			double	dtime	= (update - lastupdate)/1000.0;		
			double	speed	= dbytes/dtime;
			
			lastupdate	= update;
			lastdone	= done;
			this.speed	= this.speed==0 ? speed : this.speed*0.9 + speed*0.1;	// EMA(10)
	//		System.out.println("curspeed: "+speed+" avgspeed: "+this.speed);
		}
	}
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public String	getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 */
	public void	setId(String id)
	{
		this.id	= id;
	}

	/**
	 *  Get the state.
	 *  @return the state.
	 */
	public String getState()
	{
		return state;
	}
	
	/**
	 *  Check if transfer is a download or upload.
	 */
	public boolean isDownload()
	{
		return download;
	}

	/**
	 *  Set the transfer as a download or upload.
	 */
	public void setDownload(boolean download)
	{
		this.download	= download;
	}

	/**
	 *  Get the speed.
	 *  @return the speed.
	 */
	public double getSpeed()
	{
		return speed;
	}

	/**
	 *  Set the speed.
	 */
	public void	setSpeed(double speed)
	{
		this.speed	= speed;
	}

	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setState(String state)
	{
		if(!STATE_WAITING.equals(state) && !STATE_TRANSFERRING.equals(state) && !STATE_COMPLETED.equals(state) 
			&& !STATE_ABORTED.equals(state) && !STATE_ERROR.equals(state) && !STATE_REJECTED.equals(state))
		{
			throw new RuntimeException("Unknown state: "+state);
		}
//		if(ABORTED.equals(state))
//			System.out.println("herehhh");
		this.state = state;
	}

	/**
	 *  Check if transfer is finished.
	 */
	public boolean isFinished()
	{
		return STATE_COMPLETED.equals(state) || STATE_ABORTED.equals(state) || STATE_ERROR.equals(state) || STATE_REJECTED.equals(state);
	}
	
	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		return 31+id.hashCode();
	}

	/**
	 *  Test for equality.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof TransferInfo && ((TransferInfo)obj).getId()==getId();
	}
}
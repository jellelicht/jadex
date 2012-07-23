package jadex.base.service.settings;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.search.IResultSelector;
import jadex.bridge.service.search.ISearchManager;
import jadex.bridge.service.search.IVisitDecider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.context.IPreferences;
import jadex.commons.IValueFetcher;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import junit.framework.TestCase;


public class AndroidSettingsServiceTest extends TestCase
{

	private Map<String, Object>	_prefs;

	protected boolean			done;

	@Override
	protected void setUp() throws Exception
	{
		_prefs = new HashMap<String, Object>();
		done = false;
	}

	public void testShouldReadFromFile()
	{
		AndroidSettingsService ass = createAndroidSettingsService(false);
		ass.loadProperties();
		IFuture<Properties> properties = ass.getProperties("clockservice");
		properties.addResultListener(new DefaultResultListener<Properties>()
		{
			@Override
			public void resultAvailable(Properties result)
			{
				assertEquals("type", result.getProperties()[0].getType());
				assertEquals("other", result.getProperties()[0].getValue());
				assertEquals("delta", result.getProperties()[1].getType());
				assertEquals("0", result.getProperties()[1].getValue());
			}
		});
	}

	public void testShouldSaveAndReadPreferences() throws InterruptedException
	{
		AndroidSettingsService ass = createAndroidSettingsService(false);
		ass.loadProperties();
		// setup
		setSampleProperties(ass);
		IFuture<Properties> properties = ass.getProperties("clockservice");

		// execute
		ass.saveProperties();
		assertEquals(5, _prefs.size());
		ass = createAndroidSettingsService(false);
		ass.loadProperties();

		// verify
		ass.getProperties("clockservice").addResultListener(
				new DefaultResultListener<Properties>()
				{
					@Override
					public void resultAvailable(Properties result)
					{
						assertEquals("type",
								result.getProperties()[0].getType());
						assertEquals("system",
								result.getProperties()[0].getValue());
						assertEquals("delta",
								result.getProperties()[1].getType());
						assertEquals("100",
								result.getProperties()[1].getValue());
					}
				});

		ass.getProperties("simulationservice").addResultListener(
				new DefaultResultListener<Properties>()
				{
					@Override
					public void resultAvailable(Properties result)
					{
						assertEquals("executing",
								result.getProperties()[0].getType());
						assertEquals("false",
								result.getProperties()[0].getValue());
					}
				});

		ass.getProperties("securityservice").addResultListener(
				new DefaultResultListener<Properties>()
				{
					@Override
					public void resultAvailable(Properties result)
					{
						assertEquals("usepass",
								result.getProperties()[0].getType());
						assertEquals("true",
								result.getProperties()[0].getValue());
						assertEquals("password",
								result.getProperties()[1].getType());
						assertEquals("xyz",
								result.getProperties()[1].getValue());
						done = true;
					}
				});

		if(!done)
		{
			fail();
		}
	}

	public void testShouldPreferFileProperties() throws InterruptedException
	{
		// setup
		AndroidSettingsService ass = createAndroidSettingsService(true);
		ass.loadProperties();
		setSampleProperties(ass);
		IFuture<Properties> properties = ass.getProperties("clockservice");

		// execute
		ass.saveProperties();
		assertEquals(5, _prefs.size());
		ass = createAndroidSettingsService(true);
		ass.loadProperties();

		// verify
		ass.getProperties("clockservice").addResultListener(
				new DefaultResultListener<Properties>()
				{
					@Override
					public void resultAvailable(Properties result)
					{
						assertEquals("type",
								result.getProperties()[0].getType());
						assertEquals("other",
								result.getProperties()[0].getValue());
						assertEquals("delta",
								result.getProperties()[1].getType());
						assertEquals("0", result.getProperties()[1].getValue());
					}
				});
	}

	private void setSampleProperties(AndroidSettingsService ass)
	{
		Properties clockProperties = new Properties(null, "clockservice", null);
		Properties simulationProperties = new Properties(null,
				"simulationservice", null);
		Properties securityProperties = new Properties(null, "securityservice",
				null);

		clockProperties.addProperty(new Property("type", "system"));
		clockProperties.addProperty(new Property("delta", "100"));

		simulationProperties.addProperty(new Property("executing", "false"));

		securityProperties.addProperty(new Property("usepass", "true"));
		securityProperties.addProperty(new Property("password", "xyz"));

		ass.setProperties(clockProperties.getType(), clockProperties);
		ass.setProperties(simulationProperties.getType(), simulationProperties);
		ass.setProperties(securityProperties.getType(), securityProperties);
	}

	private AndroidSettingsService createAndroidSettingsService(
			boolean preferFileProperties)
	{
		AndroidSettingsService ass = new AndroidSettingsService(
				new internalAccess(), true, preferFileProperties);
		ass.contextService = new IContextService()
		{


			public IPreferences getSharedPreferences(String preferenceFileName)
			{
				return new IPreferences()
				{

					public void setString(String key, String value)
					{
						_prefs.put(key, value);
					}

					public String getString(String key, String defValue)
					{
						Object ret = _prefs.get(key);
						if(ret != null && ret instanceof String)
						{
							return (String)ret;
						}
						else
						{
							return defValue;
						}
					}

					public boolean getBoolean(String key, boolean defValue)
					{
						Object ret = _prefs.get(key);
						if(ret != null && ret instanceof Boolean)
						{
							return (Boolean)ret;
						}
						else
						{
							return defValue;
						}
					}

					public Map<String, ? > getAll()
					{
						return _prefs;
					}

					public boolean commit()
					{
						return true;
					}
				};
			}

			public File getFile(String name)
			{
				try
				{
					File createTempFile = File.createTempFile(this.getClass()
							.getName(), "1");
					FileWriter fw = new FileWriter(createTempFile);
					fw.write("<?xml version='1.0' encoding='utf-8'?>"
							+ "<p0:properties xmlns:p0=\"http://jadex.sourceforge.net/jadexconf\" __ID=\"0\">"
							+ "<p0:properties __ID=\"1\">"
							+ "<p0:property __ID=\"2\" type=\"type\">other</p0:property>"
							+ "<p0:property __ID=\"3\" type=\"delta\">0</p0:property>"
							+ "<type __ID=\"5\">clockservice</type>"
							+ "</p0:properties>" + "</p0:properties>");
					fw.close();
					return createTempFile;
				}
				catch(IOException e)
				{
					e.printStackTrace();
					return null;
				}
			}

			public boolean dispatchUiEvent(IJadexAndroidEvent event)
			{
				return false;
			}

			public List<InetAddress> getNetworkIps()
			{
				return null;
			}

			public void openFile(String path) throws IOException
			{
			}

		};

		return ass;
	}

	static class internalAccess implements IInternalAccess
	{

		private IComponentIdentifier	componentIdentifier	= new ComponentIdentifier(
																	"containerId");

		public IModelInfo getModel()
		{
			return null;
		}

		public String getConfiguration()
		{
			return null;
		}

		public IExternalAccess getParentAccess()
		{
			return null;
		}

		public IFuture<Collection<IExternalAccess>> getChildrenAccesses()
		{
			return null;
		}

		public IFuture<IComponentIdentifier[]> getChildrenIdentifiers()
		{
			return null;
		}

		public IComponentIdentifier getComponentIdentifier()
		{
			return componentIdentifier;
		}

		public IComponentDescription getComponentDescription()
		{
			return null;
		}

		public IServiceContainer getServiceContainer()
		{
			return new IServiceContainer()
			{

				public String getType()
				{
					return null;
				}

				public IIntermediateFuture<IService> getServices(
						ISearchManager manager, IVisitDecider decider,
						IResultSelector selector)
				{
					List<IService> list = new ArrayList<IService>();

					list.add(new IService()
					{

						public IFuture<Boolean> isValid()
						{
							return null;
						}

						public IServiceIdentifier getServiceIdentifier()
						{
							return null;
						}

						public Map<String, Object> getPropertyMap()
						{
							return null;
						}
					});
					return new IntermediateFuture<IService>(list);
				}

				public IFuture<IServiceProvider> getParent()
				{
					return null;
				}

				public IComponentIdentifier getId()
				{
					return componentIdentifier;
				}

				public IFuture<Collection<IServiceProvider>> getChildren()
				{
					return null;
				}

				public IFuture<Void> start()
				{
					return null;
				}

				public IFuture<Void> shutdown()
				{
					return null;
				}

				public void setRequiredServiceInfos(
						RequiredServiceInfo[] requiredservices)
				{
				}

				public <T> IIntermediateFuture<T> searchServices(Class<T> type,
						String scope)
				{
					return null;
				}

				public <T> IIntermediateFuture<T> searchServices(Class<T> type)
				{
					return null;
				}

				public <T> IFuture<T> searchServiceUpwards(Class<T> type)
				{
					return null;
				}

				public <T> IFuture<T> searchService(Class<T> type, String scope)
				{
					return null;
				}

				public <T> IFuture<T> searchService(Class<T> type)
				{
					return null;
				}

				public IFuture<Void> removeService(IServiceIdentifier sid)
				{
					return null;
				}

				public void removeInterceptor(
						IServiceInvocationInterceptor interceptor,
						Object service)
				{

				}

				public <T> IFuture<T> getService(Class<T> type,
						IComponentIdentifier cid)
				{
					return null;
				}

				public <T> IIntermediateFuture<T> getRequiredServices(
						String name, boolean rebind)
				{
					return null;
				}

				public <T> IIntermediateFuture<T> getRequiredServices(
						String name)
				{
					return null;
				}

				public RequiredServiceInfo[] getRequiredServiceInfos()
				{
					return null;
				}

				public RequiredServiceInfo getRequiredServiceInfo(String name)
				{
					return null;
				}

				public <T> IFuture<T> getRequiredService(String name,
						boolean rebind)
				{
					return null;
				}

				public <T> IFuture<T> getRequiredService(String name)
				{
					return null;
				}

				public IService[] getProvidedServices(Class< ? > clazz)
				{
					return null;
				}

				public IService getProvidedService(String name)
				{
					return null;
				}

				public IServiceInvocationInterceptor[] getInterceptors(
						Object service)
				{
					return null;
				}

				public IFuture<Void> addService(IInternalService service,
						ProvidedServiceInfo info)
				{
					return null;
				}

				public void addRequiredServiceInfos(
						RequiredServiceInfo[] requiredservices)
				{

				}

				public void addInterceptor(
						IServiceInvocationInterceptor interceptor,
						Object service, int pos)
				{
				}
			};
		}

		public IFuture<Map<String, Object>> killComponent()
		{
			return null;
		}

		public <T> IResultListener<T> createResultListener(
				final IResultListener<T> listener)
		{
			return new DefaultResultListener<T>()
			{

				@Override
				public void resultAvailable(T result)
				{
					listener.resultAvailable(result);
				}
			};
		}

		public <T> IIntermediateResultListener<T> createResultListener(
				IIntermediateResultListener<T> listener)
		{
			return null;
		}

		public IExternalAccess getExternalAccess()
		{
			return null;
		}

		public Logger getLogger()
		{
			return null;
		}

		public IValueFetcher getFetcher()
		{
			return null;
		}

		public IFuture<Void> addComponentListener(IComponentListener listener)
		{
			return null;
		}

		public IFuture<Void> removeComponentListener(IComponentListener listener)
		{
			return null;
		}

		public Map<String, Object> getArguments()
		{
			return null;
		}

		public Map<String, Object> getResults()
		{
			return null;
		}

		public void setResultValue(String name, Object value)
		{
		}

		public ClassLoader getClassLoader()
		{
			return null;
		}

		public <T> IFuture<T> waitForDelay(long delay, IComponentStep<T> step)
		{
			return null;
		}

		public boolean isComponentThread()
		{
			return true;
		}
	}
}

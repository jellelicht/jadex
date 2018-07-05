package jadex.examples.presentationtimer.remotecontrol;

import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.SResultListener;


public class ClientMain  {
	
	public static boolean	startedWithMain;
	
	public static void main(String[] args) {
		Future.DEBUG = true;
		ClientMain countDownClient = new ClientMain();
		countDownClient.setUp();
		startedWithMain = true;
	}
	
	public void setUp()
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getMinimalComm();

		config.setPlatformName("presentationtimer-*");
		config.setNetworkNames(new String[] { "jadexnetwork" });
		config.setNetworkSecrets(new String[] { "laxlax" });
//		config.setDht(true);

		IFuture<IExternalAccess> fut = Starter.createPlatform(config);

		fut.addResultListener(access -> {

			access.scheduleStep(ia -> {
				System.out.println("Got external platform access");
				IComponentManagementService cms = getCMS(access).get();
				System.out.println("Got cms");
				ITuple2Future<IComponentIdentifier, Map<String, Object>> fut2 = cms.createComponent("CDClient", ClientAgent.class.getName() + ".class", null);
				fut2.addTuple2ResultListener(cid -> System.out.println("Client Agent created"), SResultListener.ignoreResults());
				return Future.getEmptyFuture();
			});
		});

	}

	private IFuture<IComponentManagementService> getCMS(IExternalAccess access)
	{
		return access.scheduleStep(ia -> ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)));
	}

}

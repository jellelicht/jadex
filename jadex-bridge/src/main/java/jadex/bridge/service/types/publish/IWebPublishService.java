package jadex.bridge.service.types.publish;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

import java.net.URI;

/**
 * 
 */
@Service
public interface IWebPublishService extends IPublishService
{
	/**
	 *  Publish a servlet (without ressources).
	 */
	public IFuture<Void> publishServet(URI uri, Object servlet);
	
	/**
	 *  Publish a static page (without ressources).
	 */
	public IFuture<Void> publishHMTLPage(URI uri, String html);

}

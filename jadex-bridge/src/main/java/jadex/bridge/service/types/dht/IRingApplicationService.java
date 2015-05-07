package jadex.bridge.service.types.dht;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;


/**
 * Service to provide applications access to the ring
 * overlay network.
 */
public interface IRingApplicationService
{
	public static int	TIMEOUT	= 2112;

	/**
	 * Return the successor of this node.
	 * 
	 * @return finger entry of the successor.
	 */
	@Timeout(TIMEOUT)
	IFuture<IFinger> getSuccessor();

	/**
	 * Find the successor of a given ID in the ring.
	 * 
	 * @param id ID to find the successor of.
	 * @return The finger entry of the best closest successor.
	 */
	@Timeout(TIMEOUT)
	IFuture<IFinger> findSuccessor(IID id);

	/**
	 * Return own ID.
	 * 
	 * @return own ID.
	 */
	@Timeout(TIMEOUT)
	IFuture<IID> getId();
}

package jadex.commons.future;


/**
 * Interface for futures. Similar to Java Future interface but adds a listener
 * notification mechanism.
 */
//@Reference
public interface IFuture<E>
{
	// -------- constants --------

	/**
	 *  A future representing a completed action. Can be used as direct return
	 *  value of methods that do not perform asynchronous operations and do not
	 *  return a result value.
	 */
	public static final IFuture<Void>	DONE	= new Future<Void>((Void)null);
	
	// -------- methods --------

	/**
	 *  Test if done, i.e. result is available.
	 *  @return True, if done.
	 */
	public boolean isDone();

	/**
	 *  Get the exception, if any.
	 *  @return	The exception, if any, or null if the future is not yet done or succeeded without exception.
	 */
	public Exception	getException();

	/**
	 *  Get the result - blocking call.
	 *  @return The future result.
	 *  deprecated Use get() instead.
	 */
	public E get(ISuspendable caller);

	/**
	 *  Get the result - blocking call.
	 *  @param timeout The timeout in millis.
	 *  @return The future result.
	 *  deprecated Use get(timeout) instead.
	 */
	public E get(ISuspendable caller, long timeout);

	/**
	 *  Get the result - blocking call.
	 *  @return The future result.
	 */
	public E get();

	/**
	 *  Get the result - blocking call.
	 *  @param timeout The timeout in millis.
	 *  @return The future result.
	 */
	public E get(long timeout);

	/**
	 *  Add a result listener.
	 *  @param listener The listener.
	 */
	public void addResultListener(IResultListener<E> listener);
	
	/**
	 * Add an OnSuccessListener, which is only called on success.
	 * Exceptions will be handled by DefaultResultListener.
	 * @param listener The listener.
	 */
	public void addResultListener(IOnSuccessListener<E> listener);
	
	/**
	 * Add an OnSuccessListener, which is only called on success.
	 * @param listener The listener.
	 * @param defaultExceptionHandling Use default exception handling. If false, exceptions will be ignored
	 * unless other listeners are registered.
	 */
	public void addResultListener(IOnSuccessListener<E> listener, boolean defaultExceptionHandling);
	
	/**
	 * Add an OnExceptionListener, which is only called on exceptions.
	 * @param listener The listener.
	 */
	public void addExceptionListener(IOnExceptionListener listener);

	/**
	 *  Add a result listener by combining an OnSuccessListener and an OnExceptionListener.
	 *  @param sucListener The listener called on success.
	 *  @param exListener The listener called on exceptions.
	 */
	public void addResultListener(IOnSuccessListener<E> sucListener, IOnExceptionListener exListener);

}

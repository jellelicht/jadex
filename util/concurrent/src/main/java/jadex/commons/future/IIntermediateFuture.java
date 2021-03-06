package jadex.commons.future;


import java.util.Collection;
import java.util.NoSuchElementException;

import jadex.commons.functional.Function;

/**
 *  Future that support intermediate results.
 */
//@Reference
public interface IIntermediateFuture<E> extends IFuture<Collection <E>>
{
	// -------- constants --------

	/**
	 *  A future representing a completed action. Can be used as direct return
	 *  value of methods that do not perform asynchronous operations and do not
	 *  return a result value.
	 */
	public static final IntermediateFuture<Void>	DONE	= new IntermediateFuture<Void>((Collection<Void>)null);

	
    /**
     *  Get the intermediate results that are currently available.
     *  Non-blocking method.
     *  @return The future result.
     */
    public Collection<E> getIntermediateResults();

    /**
     *  Check if there are more results for iteration for the given caller.
     *  If there are currently no unprocessed results and future is not yet finished,
     *  the caller is blocked until either new results are available and true is returned
     *  or the future is finished, thus returning false.
     *  
     *  @return	True, when there are more intermediate results for the caller.
     */
    public boolean hasNextIntermediateResult();
	
    /**
     *  Check if there are more results for iteration for the given caller.
     *  If there are currently no unprocessed results and future is not yet finished,
     *  the caller is blocked until either new results are available and true is returned
     *  or the future is finished, thus returning false.
     *  
	 *  @param timeout The timeout in millis.
	 *  @param realtime Flag, if wait should be realtime (in constrast to simulation time).
     *  @return	True, when there are more intermediate results for the caller.
     */
    public boolean hasNextIntermediateResult(long timeout, boolean realtime);
	
    /**
     *  Iterate over the intermediate results in a blocking fashion.
     *  Manages results independently for different callers, i.e. when called
     *  from different threads, each thread receives all intermediate results.
     *  
     *  The operation is guaranteed to be non-blocking, if hasNextIntermediateResult()
     *  has returned true before for the same caller. Otherwise the caller is blocked
     *  until a result is available or the future is finished.
     *  
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getNextIntermediateResult();
    
    /**
     *  Iterate over the intermediate results in a blocking fashion.
     *  Manages results independently for different callers, i.e. when called
     *  from different threads, each thread receives all intermediate results.
     *  
     *  The operation is guaranteed to be non-blocking, if hasNextIntermediateResult()
     *  has returned true before for the same caller. Otherwise the caller is blocked
     *  until a result is available or the future is finished.
     *  
	 *  @param timeout The timeout in millis.
	 *  @param realtime Flag, if wait should be realtime (in constrast to simulation time).
     *  @return	The next intermediate result.
     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
     */
    public E getNextIntermediateResult(long timeout, boolean realtime);
    
//    /**
//     *  Iterate over the intermediate results in a blocking fashion.
//     *  Manages results independently for different callers, i.e. when called
//     *  from different threads, each thread receives all intermediate results.
//     *  
//     *  The operation is guaranteed to be non-blocking, if hasNextIntermediateResult()
//     *  has returned true before for the same caller. Otherwise the caller is blocked
//     *  until a result is available or the future is finished.
//     *  
//     *  @return	The next intermediate result.
//     *  @throws NoSuchElementException, when there are no more intermediate results and the future is finished. 
//     */
//    public E getNextIntermediateResult(ISuspendable sus);
    
    /**
	 * Add a result listener.
	 * 
	 * @param intermediateListener The intermediate listener.
	 * 
	 * @deprecated Use addResultListener()
	 */
	public void addIntermediateResultListener(IIntermediateResultListener<E> intermediateListener);
    
	/**
	 * Add a functional result listener, which called on intermediate results.
	 * Exceptions will be logged.
	 * 
	 * @param intermediateListener The intermediate listener.
	 */
	public void addIntermediateResultListener(IFunctionalIntermediateResultListener<E> intermediateListener);
    
	/**
	 * Add a functional result listener, which called on intermediate results.
	 * Exceptions will be logged.
	 * 
	 * @param intermediateListener The intermediate listener.
	 * @param finishedListener The finished listener, called when no more
	 *        intermediate results will arrive. If <code>null</code>, the finish
	 *        event will be ignored.
	 */
	public void addIntermediateResultListener(IFunctionalIntermediateResultListener<E> intermediateListener, IFunctionalIntermediateFinishedListener<Void> finishedListener);
    
	/**
	 * Add a functional result listener, which called on intermediate results.
	 * 
	 * @param intermediateListener The intermediate listener.
	 * @param exceptionListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 */
    public void addIntermediateResultListener(IFunctionalIntermediateResultListener<E> intermediateListener, IFunctionalExceptionListener exceptionListener);

	/**
	 * Add a functional result listener, which called on intermediate results.
	 * 
	 * @param intermediateListener The intermediate listener.
	 * @param finishedListener The finished listener, called when no more
	 *        intermediate results will arrive. If <code>null</code>, the finish
	 *        event will be ignored.
	 * @param exceptionListener The listener that is called on exceptions. Passing
	 *        <code>null</code> enables default exception logging.
	 */
    public void addIntermediateResultListener(IFunctionalIntermediateResultListener<E> intermediateListener, IFunctionalIntermediateFinishedListener<Void> finishedListener, IFunctionalExceptionListener exceptionListener);

    //-------- java 8 extensions --------
    
    /**
	 *  Implements async loop and applies a an async function to each element.
	 *  @param function The function.
	 *  @return True result intermediate future.
	 */
	public <R> IIntermediateFuture<R> mapAsync(Function<E, IFuture<R>> function);
	
	/**
	 *  Implements async loop and applies a an async function to each element.
	 *  @param function The function.
	 *  @return True result intermediate future.
	 */
	public <R> IIntermediateFuture<R> mapAsync(Function<E, IFuture<R>> function, Class<?> futuretype);
	
	/**
	 *  Implements async loop and applies a an async multi-function to each element.
	 *  @param function The function.
	 *  @return True result intermediate future.
	 */
	public <R> IIntermediateFuture<R> flatMapAsync(Function<E, IIntermediateFuture<R>> function);
}

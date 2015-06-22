package monads;

import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/**
 *  Monadic futures for Java.
 *  http://zeroturnaround.com/rebellabs/monadic-futures-in-java8/
 */
public class Test
{
    public static void main(String[] args)
    {
//		IFuture<String> f = $(getHello(), Test::getWorld);
//		IFuture<String> f = getHello().$(x -> getWorld(x));
        IFuture<String> f = $(getHello(), x -> getWorld(x));
        IIntermediateFuture<String> inf = $$(getABC(), x -> getD(x));

        test(Test::abc);
        
        System.out.println("result is: "+$(f));
        System.out.println("result is: "+$(inf));
    }

    public static void abc(String a)
    {
        System.out.println("abc: "+a);
    }
    
    public static IFuture<String> getHelloWorld()
    {
        return $(getHello(), x->getWorld(x));
    }

    public static IFuture<String> getHello()
    {
        return new Future<String>("hello");
    }

    public static IFuture<String> getWorld(String hello)
    {
        return new Future<String>(hello+" world");
    }

    public static IIntermediateFuture<String> getABC()
    {
        return new IntermediateFuture<>(Arrays.asList("a", "b", "c"));
    }

    public static IFuture<String> getD(String arg)
    {
        Future<String> ret = new Future<>();
        ret.setResult(arg+"_1");
        return ret;
    }

    public static <V, R> void test(final IFunctionalResultListener<R> function)
    {
    	function.resultAvailable((R)"hsa");
    }
    
    public static <V, R> IFuture<R> $(IFuture<V> orig, final Function<V, IFuture<R>> function)
    {
        Future<R> ret = new Future<>();

        System.out.println(function);
        
        Type type = ((Class<?>)function.getClass().getGenericInterfaces()[0]).getDeclaredMethods()[0].getGenericReturnType();

        if(type instanceof ParameterizedType) 
        {
            Type actualType = ((ParameterizedType) type).getActualTypeArguments()[0];
            System.out.println(actualType);
        }
        
        orig.addResultListener(new IResultListener<V>()
        {
            public void resultAvailable(V result)
            {
                IFuture<R> res = function.apply(result);
                res.addResultListener(new DelegationResultListener<R>(ret));
            }

            public void exceptionOccurred(Exception exception)
            {
                ret.setException(exception);
            }
        });

        return ret;
    }

    public static <V, R> IIntermediateFuture<R> $$(IIntermediateFuture<V> orig, final Function<V, IFuture<R>> function)
    {
        IntermediateFuture<R> ret = new IntermediateFuture<>();

        orig.addIntermediateResultListener(new IIntermediateResultListener<V>()
        {
            public void resultAvailable(Collection<V> result)
            {
                for(V v: result)
                {
                    intermediateResultAvailable(v);
                }
                finished();
            }

            public void intermediateResultAvailable(V result)
            {
                IFuture<R> res = function.apply(result);
                res.addResultListener(new IResultListener<R>()
                {
                    public void resultAvailable(R result)
                    {
                        ret.addIntermediateResult(result);
                    }

                    public void exceptionOccurred(Exception exception)
                    {
                        ret.setExceptionIfUndone(exception);
                    }
                });
            }

            public void finished()
            {
                ret.setFinished();
            }

            public void exceptionOccurred(Exception exception)
            {
                ret.setException(exception);
            }
        });

        return ret;
    }

//    public static <V, R> IIntermediateFuture<R> $$(IIntermediateFuture<V> orig, final Function<V, R> function)
//    {
//        IntermediateFuture<R> ret = new IntermediateFuture<>();
//
//        orig.addIntermediateResultListener(new IIntermediateResultListener<V>()
//        {
//            public void resultAvailable(Collection<V> result)
//            {
//                for(V v: result)
//                {
//                    intermediateResultAvailable(v);
//                }
//                finished();
//            }
//
//            public void intermediateResultAvailable(V result)
//            {
//                R res = function.apply(result);
//                ret.addIntermediateResult(res);
//            }
//
//            public void finished()
//            {
//                ret.setFinished();
//            }
//
//            public void exceptionOccurred(Exception exception)
//            {
//                ret.setException(exception);
//            }
//        });
//
//        return ret;
//    }

//    public static <V, R> IIntermediateFuture<R> $$(IIntermediateFuture<V> orig, final Function<Collection<V>, IIntermediateFuture<R>> function)
//    {
//        IntermediateFuture<R> ret = new IntermediateFuture<>();
//
//        orig.addIntermediateResultListener(new IIntermediateResultListener<V>()
//        {
//            public void resultAvailable(Collection<V> result)
//            {
//                IIntermediateFuture<R> res = function.apply(result);
//
////                for(V v: result)
////                {
////                    intermediateResultAvailable(v);
////                }
//                finished();
//            }
//
//            public void intermediateResultAvailable(V result)
//            {
////                IIntermediateFuture<R> res = function.apply(result);
////                res.addResultListener(new IntermediateDelegationResultListener<R>(ret));
//            }
//
//            public void finished()
//            {
//                ret.setFinished();
//            }
//
//            public void exceptionOccurred(Exception exception)
//            {
//                ret.setException(exception);
//            }
//        });
//
//        return ret;
//    }

    public static <T> T $(IFuture<T> fut)
    {
        return fut.get();
    }

}

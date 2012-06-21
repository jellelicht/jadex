package jadex.micro.testcases.threading;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.testcases.TestAgent;

import java.util.Collection;

/**
 * 
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="ts", type=ITestService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL)),
})
public class InitiatorAgent extends TestAgent
{
	/**
	 *  Perform the tests.
	 */
	protected IFuture<Void> performTests(final Testcase tc)
	{
		final Future<Void> ret = new Future<Void>();
		
		testLocal(1, 10000).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
		{
			public void customResultAvailable(TestReport result)
			{
				tc.addReport(result);
				testRemote(2, 1000).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<TestReport, Void>(ret)
				{
					public void customResultAvailable(TestReport result)
					{
						tc.addReport(result);
						ret.setResult(null);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Test local.
	 */
	protected IFuture<TestReport> testLocal(final int testno, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		performTest(agent.getServiceProvider(), agent.getComponentIdentifier().getRoot(), testno, max, true)
			.addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
		{
			public void customResultAvailable(final TestReport result)
			{
				ret.setResult(result);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Test remote.
	 */
	protected IFuture<TestReport> testRemote(final int testno, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		createPlatform(new String[]{"-ssltcptransport", "false"}).addResultListener(agent.createResultListener(
			new ExceptionDelegationResultListener<IExternalAccess, TestReport>(ret)
		{
			public void customResultAvailable(final IExternalAccess platform)
			{
				performTest(platform.getServiceProvider(), platform.getComponentIdentifier(), testno, max, false)
					.addResultListener(agent.createResultListener(new DelegationResultListener<TestReport>(ret)
				{
					public void customResultAvailable(final TestReport result)
					{
						platform.killComponent();
//							.addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, TestReport>(ret)
//						{
//							public void customResultAvailable(Map<String, Object> v)
//							{
//								ret.setResult(result);
//							}
//						});
						ret.setResult(result);
					}
				}));
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Perform the test. Consists of the following steps:
	 *  Create provider agent
	 *  Call methods on it
	 */
	protected IFuture<TestReport> performTest(final IServiceProvider provider, final IComponentIdentifier root, final int testno, final int max, final boolean hassectrans)
	{
		final Future<TestReport> ret = new Future<TestReport>();

		final Future<TestReport> res = new Future<TestReport>();
		
		ret.addResultListener(new DelegationResultListener<TestReport>(res)
		{
			public void exceptionOccurred(Exception exception)
			{
				TestReport tr = new TestReport("#"+testno, "Tests if secure transport works.");
				tr.setReason(exception.getMessage());
				super.resultAvailable(tr);
			}
		});
		
		final Future<Collection<Tuple2<String, Object>>> resfut = new Future<Collection<Tuple2<String, Object>>>();
		IResultListener<Collection<Tuple2<String, Object>>> reslis = new DelegationResultListener<Collection<Tuple2<String,Object>>>(resfut);
		
		createComponent(provider, "jadex/micro/testcases/threading/ProviderAgent.class", root, reslis)
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, TestReport>(ret)
		{
			public void customResultAvailable(final IComponentIdentifier cid) 
			{
				callService(cid, hassectrans, testno, max).addResultListener(new DelegationResultListener<TestReport>(ret));
			}
		});
		
		return res;
	}
	
	/**
	 *  Call the service methods.
	 */
	public IFuture<TestReport> callService(IComponentIdentifier cid, final boolean hassectrans, int testno, final int max)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		final TestReport tr = new TestReport("#"+testno, "Test if secure transmission works "+(hassectrans? "with ": "without ")+"secure transport.");
		
		IFuture<ITestService> fut = agent.getServiceContainer().getService(ITestService.class, cid);
		fut.addResultListener(new ExceptionDelegationResultListener<ITestService, TestReport>(ret)
		{
			public void customResultAvailable(final ITestService ts)
			{
				final long start = System.currentTimeMillis();
				invoke(ts, 0, max).addResultListener(new ExceptionDelegationResultListener<Void, TestReport>(ret)
				{
					public void customResultAvailable(Void result)
					{
						long dur = System.currentTimeMillis()-start;
						System.out.println("Needed per call [ms]: "+((double)dur)/max);
						tr.setSucceeded(true);
						ret.setResult(tr);
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> invoke(final ITestService ts, final int i, final int max)
	{
		final Future<Void> ret = new Future<Void>();
		
		final IComponentIdentifier caller = IComponentIdentifier.LOCAL.get();
		
		ts.test().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				if(!caller.equals(IComponentIdentifier.LOCAL.get()))
				{
					System.out.println("err: "+caller+" "+IComponentIdentifier.LOCAL.get());
				}
				else
				{
					System.out.println("ok: "+i);
				}
				
				if(i<max)
				{
					invoke(ts, i+1, max).addResultListener(new DelegationResultListener<Void>(ret));
				}
				else
				{
					ret.setResult(null);
				}
			}
		
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				resultAvailable(null);
			}
		});
		
		return ret;
	}
}

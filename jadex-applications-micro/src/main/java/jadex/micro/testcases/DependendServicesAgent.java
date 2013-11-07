package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Starts two agents a and b
 *  a has service sa
 *  b has service sb
 *  init of service sb searches for sa and uses the service
 *
 *  (problem is that component a has finished its init but must execute the service call for sb)
 */
@Description("Test if services of (earlier) sibling components can be found and used.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@ComponentTypes({
    @ComponentType(name="a", filename="jadex.micro.testcases.AAgent.class"),
    @ComponentType(name="b", filename="jadex.micro.testcases.BAgent.class")
})
@Configurations(@Configuration(name="def", components={
    @Component(type="a"),
    @Component(type="b")
}))
public class DependendServicesAgent extends MicroAgent
{
    /**
     *  Init code.
     */
    public IFuture<Void> agentCreated()
    {
        final Future<Void> ret = new Future<Void>();
        getChildrenAccesses().addResultListener(createResultListener(new DefaultResultListener<Collection<IExternalAccess>>()
        {
            public void resultAvailable(Collection<IExternalAccess> result)
            {
            	IExternalAccess[] childs = (IExternalAccess[])result.toArray(new IExternalAccess[0]);
//   			System.out.println("childs: "+SUtil.arrayToString(childs));
                final CollectionResultListener<Collection<TestReport>> lis = new CollectionResultListener<Collection<TestReport>>(childs.length, true, 
                	createResultListener(new DefaultResultListener<Collection<Collection<TestReport>>>()
                {
                    public void resultAvailable(Collection<Collection<TestReport>> result)
                    {
//						System.out.println("fini: "+result);
						List<TestReport> tests = new ArrayList<TestReport>();
//						Collection<TestReport> col = (Collection<TestReport>)result;
						for(Iterator<Collection<TestReport>> it=result.iterator(); it.hasNext(); )
						{
							Collection<TestReport> tmp = (Collection<TestReport>)it.next();
							tests.addAll(tmp);
						}
						setResultValue("testresults", new Testcase(tests.size(), (TestReport[])tests.toArray(new TestReport[tests.size()])));
						
						killAgent();
                    }
                }));

                for(int i=0; i<childs.length; i++)
                {
                    final IExternalAccess child = childs[i];
                    child.subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
						.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
					{
						public void intermediateResultAvailable(IMonitoringEvent result)
						{
							child.getResults().addResultListener(createResultListener(new IResultListener<Map<String, Object>>()
                            {
                                public void resultAvailable(Map<String, Object> res)
                                {
//                                  System.out.println("del: "+child.getComponentIdentifier()+" "+result);
//                                    Map res = (Map)result;
                                    List<TestReport> tests = (List<TestReport>)res.get("testcases");
                                    lis.resultAvailable(tests);
                                }
                                public void exceptionOccurred(Exception exception)
                                {
                                	lis.exceptionOccurred(exception);
                                }
                            }));
						}
					}));
                }
                ret.setException(null);
            }
        }));
        return ret;
    }

    /**
     *  The agent body.
     */
    public IFuture<Void> executeBody()
    {
        getChildrenAccesses().addResultListener(createResultListener(new DefaultResultListener<Collection<IExternalAccess>>()
        {
            public void resultAvailable(Collection<IExternalAccess> result)
            {
                IExternalAccess[] childs = (IExternalAccess[])result.toArray(new IExternalAccess[0]);
                for(int i=0; i<childs.length; i++)
                {
                    childs[i].killComponent();
                }
            }
        }));
        
        return new Future<Void>(); // never kill?
    }

	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture<Collection<IExternalAccess>> getChildrenAccesses()
	{
		final Future<Collection<IExternalAccess>> ret = new Future<Collection<IExternalAccess>>();
		
		SServiceProvider.getServiceUpwards(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Collection<IExternalAccess>>(ret)
		{
			public void customResultAvailable(IComponentManagementService result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				cms.getChildren(getComponentIdentifier()).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier[], Collection<IExternalAccess>>(ret)
				{
					public void customResultAvailable(IComponentIdentifier[] children)
					{
						IResultListener<IExternalAccess>	crl	= new CollectionResultListener<IExternalAccess>(children.length, true,
							new DelegationResultListener<Collection<IExternalAccess>>(ret));
						for(int i=0; !ret.isDone() && i<children.length; i++)
						{
							cms.getExternalAccess(children[i]).addResultListener(crl);
						}
					}
				});
			}
		});
		
		return ret;
	}
}


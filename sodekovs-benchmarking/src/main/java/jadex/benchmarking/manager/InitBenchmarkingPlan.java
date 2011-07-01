package jadex.benchmarking.manager;

import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.benchmarking.helper.Constants;
import jadex.benchmarking.helper.Methods;
import jadex.benchmarking.logger.ScheduleLogger;
import jadex.benchmarking.model.Schedule;
import jadex.benchmarking.model.SemanticCondition;
import jadex.benchmarking.model.Sequence;
import jadex.benchmarking.model.SuTinfo;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import sodekovs.util.gnuplot.persistence.LogDAO;
import sodekovs.util.math.GetRandom;
import sodekovs.util.misc.AgentMethods;
import sodekovs.util.misc.EvaluateExpression;
import sodekovs.util.misc.GlobalConstants;
import sodekovs.util.misc.XMLHandler;

public class InitBenchmarkingPlan extends Plan {

	private IComponentManagementService cms = null;
	private IClockService clockservice = null;
	private ArrayList<Sequence> sortedSequenceList = null;
	// Component Identifier of System Under Test
	private IComponentIdentifier sutCID = null;
	// Exta of System Under Test
	private IExternalAccess sutExta = null;
	// Space of System Under Test
	private AbstractEnvironmentSpace sutSpace = null;
	// Component Identifier of scheduler
	private IComponentIdentifier schedulerCID = null;
	//private Log events
	private ScheduleLogger scheduleLogger = null;

	public void body() {
		cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		clockservice = (IClockService) SServiceProvider.getService(getScope().getServiceContainer(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);

		String benchmarkDescription = (String) getBeliefbase().getBelief("scheduleDescriptionFile").getFact();
		System.out.println("#InitBench# Init Benchmark Agent with configuration file: " + benchmarkDescription);
		Schedule benchConf = (Schedule) XMLHandler.parseXMLFromXMLFile(benchmarkDescription, Schedule.class);
		getBeliefbase().getBelief("schedule").setFact(benchConf);

		// Create list of sequences, ordered by their start time
		sortedSequenceList = (ArrayList<Sequence>) benchConf.getSequences().getSequence();
		createOrderedSequenceList(sortedSequenceList);

		// Start System under Test (SuT) if required, i.e. has not been started by another component yet.
		// SUT is started in suspend mode.
		if (benchConf.getSytemUnderTest() != null) {
			startSuT(benchConf);
		}

		getBeliefbase().getBelief("suTinfo").setFact(new SuTinfo(sortedSequenceList, sutCID, sutExta, sutSpace));

		//initLogger
		//Attention: Logger has still to be initialized! (starttime and clockservice)
		scheduleLogger = new ScheduleLogger();		

		
		// Start scheduler, that handles the execution of the sequences of the conducted benchmark.
		// Scheduler is started in suspend mode.
		startScheduler();
	
		// Resume SuT
		cms.resumeComponent(sutCID).get(this);
		
		// start warm up phase, if defined. Schedule starts after this phase its execution
		if (benchConf.getWarmUpTime() != null) {
			waitFor(benchConf.getWarmUpTime());
//			myLogger.log("Warm-up finished");
		}
		
	
		// Resume scheduler
		cms.resumeComponent(schedulerCID).get(this);
		getBeliefbase().getBelief("benchmarkStatus").setFact(Constants.RUNNING);
//		myLogger.log("Resumed Scheduler");

		// TODO: Hack: Synchronize start time!
		long startTime = clockservice.getTime();
		sutSpace.setProperty("BENCHMARK_REAL_START_TIME_OF_SIMULATION", startTime);

		// Handle termination of benchmark
		terminateBenchmark(benchConf);
		getBeliefbase().getBelief("benchmarkStatus").setFact(Constants.TERMINATED);
		scheduleLogger.log(Constants.PREPARE_GNUPLOT_SUFFIX);		
		persistLogs(scheduleLogger.getFileName(), benchConf);
//		ConnectionManager.getInstance().executeStatement("Over and out");
	}

	/*
	 * Start System under Test. Required if system has not been started yet.
	 */
	private void startSuT(Schedule schedule) {

		HashMap<String, String> sutProperties = Methods.propertyListToHashMap(schedule.getSytemUnderTest().getProperties().getProperty());

		// create SuT in suspended modus
		IFuture fut = cms.createComponent(schedule.getName() + GetRandom.getRandom(100000), sutProperties.get(Constants.APPLICATION_FILE_PATH),
				new CreationInfo(sutProperties.get(Constants.APPLICATION_COONFIGURATION), new HashMap(), null, true, false), null);
		sutCID = (IComponentIdentifier) fut.get(this);
		sutExta = (IExternalAccess) cms.getExternalAccess(sutCID).get(this);
		fut = sutExta.getExtension(sutProperties.get(Constants.SPACE_NAME));
		sutSpace = (AbstractEnvironmentSpace) fut.get(this);
	}

	/*
	 * Start scheduler in suspended mode.
	 */
	private void startScheduler() {
		HashMap args = new HashMap();
		args.put(Constants.SUT_INFO, new SuTinfo(sortedSequenceList, sutCID, sutExta, sutSpace));
		args.put(Constants.SCHEDULE_LOGGER, scheduleLogger);

		IFuture fut = cms.createComponent("Scheduler" + GetRandom.getRandom(100000), Constants.PATH_OF_SCHEDULER, new CreationInfo(null, args, null, true, false), null);
		schedulerCID = (IComponentIdentifier) fut.get(this);
	}

	/*
	 * Handle termination of benchmark
	 * 
	 * @param benchConf
	 */
	private void terminateBenchmark(Schedule benchConf) {
		if (benchConf.getTerminateCondition() != null) {
			if (benchConf.getTerminateCondition().getTerminationTime() != null) {
				waitFor(benchConf.getTerminateCondition().getTerminationTime().getValue());
				System.out.println("#InitBenchmarkingPlan# Benchmark terminated according to specified termination time.");
				destroySuT();
			} else if (benchConf.getTerminateCondition().getSemanticCondition() != null) {
				SemanticCondition semCond = benchConf.getTerminateCondition().getSemanticCondition();
				boolean terminate = false;
				// HACK: Need a observer / listener instead evaluating expression every 1000ms
				while (true) {
					waitFor(1000);

					// Hack/Limitations: Works right now only for single objects but not for all of that type...
					// Additionally: only one part of the equation can be an object...
					if (semCond.getObjectSource().getType().equalsIgnoreCase(GlobalConstants.ISPACE_OBJECT)) {
						terminate = EvaluateExpression.evaluate(sutSpace, semCond.getCondition(), semCond.getObjectSource().getName(), semCond.getObjectSource().getType());
					} else {
						IComponentIdentifier agentIdentifier = AgentMethods.getIComponentIdentifier(sutSpace, semCond.getObjectSource().getName());
						IFuture fut = cms.getExternalAccess(agentIdentifier);
						IExternalAccess exta = (IExternalAccess) fut.get(this);

						IOAVState state = ((ElementFlyweight) exta).getState();
						Object rCapability = ((ElementFlyweight) exta).getScope();

						// Evaluate condition/expression
						OAVBDIFetcher fetcher = new OAVBDIFetcher(state, rCapability);
						terminate = EvaluateExpression.evaluateExpression(fetcher, semCond.getCondition());
					}

					// Experiment has reached Target Function. Terminate
					if (terminate) {
						System.out.println("#InitBenchmarkingPlan# Terminate experiment: Semantic termination condition has been evaluated being true.");
						break;
					}
				}
				// destroy sytem under test
				destroySuT();
			} else {
				System.out.println("#InitBenchmarkingPlan# Error: NO termination condition specified. Benchmark will not be automatically terminated by BenchmarkingAgent.");
			}
		} else {
			System.out.println("#InitBenchmarkingPlan# NO termination condition specified. Benchmark will not be automatically terminated by BenchmarkingAgent.");
		}
	}

	/*
	 * Destroy SuT
	 */
	private void destroySuT() {

		cms.destroyComponent(schedulerCID).get(this);
		cms.destroyComponent(sutExta.getComponentIdentifier()).get(this);
	}

	/*
	 * Get time stamp relative to start of benchmark (without warm up phase)
	 * 
	 * @return
	 */
	private long getTimestamp() {
		long starttime = ((Long) sutSpace.getProperty("BENCHMARK_REAL_START_TIME_OF_SIMULATION")).longValue();
		return clockservice.getTime() - starttime;
	}
	
	private void persistLogs(String fileName, Schedule benchConf){
//		ConnectionManager conMgr = new ConnectionManager();
//		conMgr.storeGnuPlotLogs(fileName,benchConf.getType(),benchConf.getName(), scheduleLogger.getTimestamp());
		LogDAO.getInstance().insertNewGnuPlotLog(fileName,benchConf.getType(),benchConf.getName(), scheduleLogger.getTimestamp());
		
	}

	/*
	 * Returns the list of sequences events ascendingly ordered by relative start time.
	 */
	private void createOrderedSequenceList(ArrayList<Sequence> list) {
		Collections.sort(list, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return Long.valueOf(((Sequence) arg0).getStarttime()).compareTo(Long.valueOf(((Sequence) arg1).getStarttime()));
			}
		});
	}
}

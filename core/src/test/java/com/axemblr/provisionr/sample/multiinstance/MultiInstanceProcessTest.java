package com.axemblr.provisionr.sample.multiinstance;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class MultiInstanceProcessTest {

	private static final Logger LOG = LoggerFactory
			.getLogger(MultiInstanceProcessTest.class);

	private final String PROCESS_NAME = "multiInstanceSample";

	private ProcessEngine engine;
	private RuntimeService runtimeService;
	private ExecutorService executorService;

	@Before
	public void setUp() {
		engine = ProcessEngineConfiguration
				.createStandaloneInMemProcessEngineConfiguration()
				.setJobExecutorActivate(true) // needed for async jobs
				.buildProcessEngine();

		engine.getRepositoryService()
				.createDeployment()
				.addClasspathResource(
						"diagrams/multi-instance-sample.bpmn20.xml").deploy();

		runtimeService = engine.getRuntimeService();
		executorService = Executors.newSingleThreadExecutor();
	}

	@After
	public void tearDown() {
		executorService.shutdown();
		engine.close();
	}

	@Test
	public void testBuildAndRunMultiInstanceProcess() throws Exception {

		final String businessKey = "j-1234";

		final Map<String, Object> variables = Maps.newHashMap();

		ProcessInstance instance = runtimeService.startProcessInstanceByKey(
				PROCESS_NAME, businessKey, variables);

		Assert.assertEquals(instance.getBusinessKey(), businessKey);
		waitForProcess(instance);
		Assert.assertEquals(instance.isEnded(), true);

		HistoricProcessInstance historicProcessInstance = Iterables
				.getOnlyElement(engine.getHistoryService()
						.createHistoricProcessInstanceQuery()
						.processInstanceBusinessKey(businessKey).list());

		LOG.info("Process took {}",
				historicProcessInstance.getDurationInMillis());
	}

	private void waitForProcess(final ProcessInstance instance)
			throws InterruptedException {
		ProcessInstance localInstance;
		boolean keepRunningQuery = true;
		while (keepRunningQuery) {
			localInstance = engine.getRuntimeService()
					.createProcessInstanceQuery()
					.processInstanceBusinessKey(instance.getBusinessKey())
					.singleResult();
			if (localInstance != null) {
				LOG.info("Still waiting: {} ", localInstance.isEnded());
				keepRunningQuery = !localInstance.isEnded();
			} else {
				keepRunningQuery = false;
			}
			TimeUnit.SECONDS.sleep(1);
		}
	}
}

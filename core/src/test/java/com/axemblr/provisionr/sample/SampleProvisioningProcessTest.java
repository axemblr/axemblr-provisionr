package com.axemblr.provisionr.sample;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class SampleProvisioningProcessTest {

    private static final String PROCESS_NAME = "sample";

    private ProcessEngine engine;
    private RuntimeService runtimeService;

    @Before
    public void setUp() {
        engine = new StandaloneInMemProcessEngineConfiguration().buildProcessEngine();
        engine.getRepositoryService().createDeployment()
            .addClasspathResource("diagrams/sample.bpmn20.xml").deploy();
        runtimeService = engine.getRuntimeService();
    }

    @After
    public void tearDown() {
        engine.close();
    }

    @Test
    public void testBuildAndRunASimpleProcess() throws Exception {
        String jobId = "j-" + UUID.randomUUID();

        Map<String, Object> variables = Maps.newHashMap();
        variables.put("provisioningTimeout", "PT10M" /* 10 minutes */);

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_NAME, jobId, variables);
        assertEquals(instance.getBusinessKey(), jobId);
        waitForProcessToFinish(instance);
    }

    public void waitForProcessToFinish(ProcessInstance instance) throws InterruptedException {
        while (!instance.isEnded()) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

}

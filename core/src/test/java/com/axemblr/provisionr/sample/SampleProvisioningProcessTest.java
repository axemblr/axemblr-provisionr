package com.axemblr.provisionr.sample;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti-memory-context.cfg.xml")
@Ignore
public class SampleProvisioningProcessTest {

    private static final String PROCESS_NAME = "sample";

    @Autowired
    private RuntimeService runtimeService;

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

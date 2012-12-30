/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axemblr.provisionr.sample.multiinstance;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
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

public class MultiInstanceProcessTest {

    private static final Logger LOG = LoggerFactory.getLogger(MultiInstanceProcessTest.class);

    private final String PROCESS_NAME = "multiInstance";

    private ProcessEngine engine;
    private RuntimeService runtimeService;

    @Before
    public void setUp() {
        engine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
            .setJobExecutorActivate(true) // needed for async jobs
            .setHistory(ProcessEngineConfiguration.HISTORY_FULL)
            .buildProcessEngine();

        engine.getRepositoryService()
            .createDeployment()
            .addClasspathResource("diagrams/multiInstance.bpmn20.xml")
            .addClasspathResource("diagrams/helloDude.bpmn20.xml")
            .deploy();

        runtimeService = engine.getRuntimeService();
    }

    @After
    public void tearDown() {
        engine.close();
    }

    @Test
    public void testBuildAndRunMultiInstanceProcess() throws Exception {
        final String businessKey = "j-1234";

        final List<String> people = Lists.newArrayList("Andrei", "Ioan",
            "Eugen", "Alina", "Mihai", "Ciociolina");

        final Map<String, Object> variables = Maps.newHashMap();
        variables.put("people", people);

        SpawnProcesses.runtimeService.set(runtimeService);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
            PROCESS_NAME, businessKey, variables);

        Assert.assertEquals(instance.getBusinessKey(), businessKey);
        waitForProcess(instance);
        // after the process has ended we should not be able to get a
        // ProcessInstance but a HistoricProcessInstance

        HistoricProcessInstance historicProcessInstance = engine
            .getHistoryService().createHistoricProcessInstanceQuery()
            .processInstanceBusinessKey(businessKey).singleResult();

        LOG.info("Process took {} ms", historicProcessInstance.getDurationInMillis());
    }

    private void waitForProcess(final ProcessInstance instance) throws InterruptedException {
        ProcessInstance localInstance;
        boolean keepRunningQuery = true;
        while (keepRunningQuery) {
            localInstance = getProcessInstanceByBusinessKey(instance);
            keepRunningQuery = computeWaitCondition(localInstance);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private boolean computeWaitCondition(ProcessInstance localInstance) {
        if (localInstance != null) {
            return !localInstance.isEnded();
        } else {
            return false;
        }
    }

    private ProcessInstance getProcessInstanceByBusinessKey(
        final ProcessInstance instance) {
        return engine.getRuntimeService().createProcessInstanceQuery()
            .processInstanceBusinessKey(instance.getBusinessKey())
            .singleResult();
    }
}

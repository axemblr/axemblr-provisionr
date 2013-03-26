/*
 * Copyright (c) 2013 S.C. Axemblr Software Solutions S.R.L
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

package com.axemblr.provisionr.core.activities;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.axemblr.provisionr.test.ProcessVariablesCollector;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.junit.Test;

public class SpawnProcessForEachMachineTest {

    private static final String EMPTY_PROCESS_KEY = "empty";
    private static final String RESULT = "result";

    private static final String BUSINESS_KEY = UUID.randomUUID().toString();

    @Test
    public void testSpawnSampleProcessForLocalhost() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        Pool pool = mock(Pool.class, withSettings().serializable());
        Software software = mock(Software.class, withSettings().serializable());
        when(software.isCachedImage()).thenReturn(false);
        when(pool.getSoftware()).thenReturn(software);
        when(execution.getVariable(eq(CoreProcessVariables.POOL))).thenReturn(pool);
        when(execution.getVariable(eq(CoreProcessVariables.POOL_BUSINESS_KEY))).thenReturn(BUSINESS_KEY);

        List<Machine> machines = Lists.newArrayList(
            Machine.builder().localhost().createMachine(),
            Machine.builder().localhost().externalId("local-2").createMachine()
        );
        when(execution.getVariable(eq(CoreProcessVariables.MACHINES))).thenReturn(machines);

        ProcessVariablesCollector collector = new ProcessVariablesCollector();
        collector.install(execution);

        ProcessEngine processEngine = new StandaloneInMemProcessEngineConfiguration()
            .setJobExecutorActivate(true).buildProcessEngine();
        processEngine.getRepositoryService().createDeployment()
            .addClasspathResource("diagrams/empty.bpmn20.xml").deploy();

        try {
            JavaDelegate delegate = new SpawnProcessForEachMachine(processEngine, EMPTY_PROCESS_KEY, "test", RESULT);
            delegate.execute(execution);

            @SuppressWarnings("unchecked")
            List<String> processInstanceIds = (List<String>) collector.getVariable(RESULT);

            assertThat(processInstanceIds).hasSize(2);

        } finally {
            processEngine.close();
        }
    }

}

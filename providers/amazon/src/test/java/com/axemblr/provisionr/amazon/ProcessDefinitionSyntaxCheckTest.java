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

package com.axemblr.provisionr.amazon;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessDefinitionSyntaxCheckTest {

    private ProcessEngine processEngine;

    @Before
    public void setUp() {
        processEngine = ProcessEngineConfiguration
            .createStandaloneInMemProcessEngineConfiguration().buildProcessEngine();
    }

    @After
    public void tearDown() {
        processEngine.close();
    }

    @Test
    public void testDeployment() {
        assertCanBeDeployed("OSGI-INF/activiti/amazonPoolManagement.bpmn20.xml");
        assertCanBeDeployed("OSGI-INF/activiti/amazonMachineSetup.bpmn20.xml");
    }

    private void assertCanBeDeployed(String resource) {
        Deployment deployment = processEngine.getRepositoryService().createDeployment()
            .addClasspathResource(resource).deploy();

        assertThat(deployment).isNotNull();
    }
}

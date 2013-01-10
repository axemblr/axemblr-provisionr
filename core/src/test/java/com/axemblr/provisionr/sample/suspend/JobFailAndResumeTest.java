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

package com.axemblr.provisionr.sample.suspend;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JobFailAndResumeTest {

    private static String PROCESS_NAME = "failAndResume";

    private ProcessEngine engine;
    private RuntimeService runtimeService;

    @Before
    public void setUp() throws Exception {
        engine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
            .setJobExecutorActivate(true)
            .buildProcessEngine();

        engine.getRepositoryService().createDeployment()
            .addClasspathResource("diagrams/failAndResume.bpmn20.xml").deploy();

        runtimeService = engine.getRuntimeService();
    }

    @After
    public void tearDown() throws Exception {
        engine.close();
    }

    @Test
    public void testProcessShouldSuspendAfter3FailsAndSucceedAfterActivation() throws Exception {
        String jobId = UUID.randomUUID().toString();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_NAME, jobId,
            ImmutableMap.<String, Object>of("failTask", new FailingTask()));
        assertThat(processInstance.getBusinessKey()).isEqualTo(jobId);

        TimeUnit.SECONDS.sleep(1);

        assertThat(FailingTask.FAIL_COUNT.get()).isEqualTo(3);
        Job job = engine.getManagementService().createJobQuery().withException().singleResult();
        engine.getManagementService().setJobRetries(job.getId(), JobEntity.DEFAULT_RETRIES);

        assertThatJobGetsExecutedBeforeTimeout(10);
        assertThat(FailingTask.FAIL_COUNT.get()).isEqualTo(4);
    }

    private void assertThatJobGetsExecutedBeforeTimeout(int timeoutInSeconds) throws InterruptedException {
        checkArgument(timeoutInSeconds > 0, "supply positive");
        int i = 0;
        while (i < timeoutInSeconds) {
            if (FailingTask.FAIL_COUNT.get() == 4) return;
            TimeUnit.SECONDS.sleep(1);
            i++;
        }
        fail("Timeout exceeded");
    }
}

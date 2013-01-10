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

package com.axemblr.provisionr.core.activiti;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.activiti.engine.impl.jobexecutor.DefaultJobExecutor;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class ConfigurableFailedJobCommandFactoryTest {

    @Test
    public void testConfigurableNumberOfRetries() throws InterruptedException {
        DefaultJobExecutor jobExecutor = new DefaultJobExecutor();

        jobExecutor.setCorePoolSize(2);
        jobExecutor.setQueueSize(2);
        jobExecutor.setMaxJobsPerAcquisition(5);
        jobExecutor.setWaitTimeInMillis(50);
        jobExecutor.setLockTimeInMillis(180000);

        ProcessEngine processEngine = new StandaloneInMemProcessEngineConfiguration()
            .setJobExecutorActivate(true)
            .setJobExecutor(jobExecutor)
            .setFailedJobCommandFactory(new ConfigurableFailedJobCommandFactory(2, 1))
            .buildProcessEngine();

        processEngine.getRepositoryService().createDeployment()
            .addClasspathResource("diagrams/alwaysFail.bpmn20.xml").deploy();

        Stopwatch stopwatch = new Stopwatch().start();
        processEngine.getRuntimeService().startProcessInstanceByKey("alwaysFail");

        while (AlwaysFailTask.COUNTER.get() != 3 /* = 1 normal execution + 2 retries */) {
            TimeUnit.SECONDS.sleep(1);
        }

        stopwatch.stop();
        assertThat(stopwatch.elapsedTime(TimeUnit.SECONDS)).isGreaterThanOrEqualTo(2);

        processEngine.close();
    }
}

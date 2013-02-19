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

package com.axemblr.provisionr.core.activities;

import com.axemblr.provisionr.test.ProcessVariablesCollector;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckProcessesEndedTest extends CheckProcessesTest {

    private static final String PROCESS_IDS = "process_ids";

    private static final String RESULT = "result";

    @Test
    public void testWithAListOfEndedProcesses() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable(eq(PROCESS_IDS))).thenReturn(Lists.newArrayList("1", "2"));

        ProcessVariablesCollector collector = new ProcessVariablesCollector();
        collector.install(execution);

        RuntimeService runtimeService = mockRuntimeService(ImmutableMap.of(
            "1", mockProcessInstance(/* ended= */ true),
            "2", mockProcessInstance(/* ended= */ true)
        ));

        JavaDelegate delegate = new CheckProcessesEnded(runtimeService, PROCESS_IDS, RESULT);
        delegate.execute(execution);

        assertThat((Boolean) collector.getVariable(RESULT)).isTrue();
    }

    @Test
    public void testWithOneEndedAndOneStillRunning() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable(eq(PROCESS_IDS))).thenReturn(Lists.newArrayList("1", "2"));

        ProcessVariablesCollector collector = new ProcessVariablesCollector();
        collector.install(execution);

        RuntimeService runtimeService = mockRuntimeService(ImmutableMap.of(
            "1", mockProcessInstance(/* ended= */ true),
            "2", mockProcessInstance(/* ended= */ false)
        ));

        JavaDelegate delegate = new CheckProcessesEnded(runtimeService, PROCESS_IDS, RESULT);
        delegate.execute(execution);

        assertThat((Boolean) collector.getVariable(RESULT)).isFalse();
    }

    /**
     * We consider an invalid process instance ID as ended by default
     */
    @Test
    public void testWithOneInvalidProcessId() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable(eq(PROCESS_IDS))).thenReturn(Lists.newArrayList("1", "invalid"));

        ProcessVariablesCollector collector = new ProcessVariablesCollector();
        collector.install(execution);

        RuntimeService runtimeService = mockRuntimeService(ImmutableMap.of(
            "1", mockProcessInstance(/* ended= */ true)), "invalid");

        JavaDelegate delegate = new CheckProcessesEnded(runtimeService, PROCESS_IDS, RESULT);
        delegate.execute(execution);

        assertThat((Boolean) collector.getVariable(RESULT)).isTrue();
    }

}

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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.axemblr.provisionr.test.ProcessVariablesCollector;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

public class KillMachineSetUpProcessesTest extends CheckProcessesTest {

    private static final String PROCESS_IDS = "process_ids";

    @Test
    public void testWithAListOfEndedProcesses() throws Exception {
        RuntimeService runtimeService = runTest(Lists.newArrayList("1", "2"), ImmutableMap.of(
            "1", mockProcessInstance(/* ended= */ true),
            "2", mockProcessInstance(/* ended= */ true)
        ));
        verify(runtimeService, never()).deleteProcessInstance(anyString(), anyString());
    }

    @Test
    public void testWithAListOfActiveProcesses() throws Exception {
        RuntimeService runtimeService = runTest(Lists.newArrayList("1", "2"), ImmutableMap.of(
            "1", mockProcessInstance(/* ended= */ false),
            "2", mockProcessInstance(/* ended= */ false)
        ));
        verify(runtimeService, times(2)).deleteProcessInstance(anyString(), anyString());
    }

    @Test
    public void testWithAListOfActiveAndInactiveProcesses() throws Exception {
        RuntimeService runtimeService = runTest(Lists.newArrayList("1", "2"), ImmutableMap.of(
            "1", mockProcessInstance(/* ended= */ true),
            "2", mockProcessInstance(/* ended= */ false)
        ));

        verify(runtimeService, times(1)).deleteProcessInstance(anyString(), anyString());
    }

    @Test
    public void testWithAListOfActiveAndInvalidProcesses() throws Exception {
        RuntimeService runtimeService = runTest(Lists.newArrayList("1", "invalid"), ImmutableMap.of(
            "1", mockProcessInstance(/* ended= */ false)));
        verify(runtimeService, times(1)).deleteProcessInstance(anyString(), anyString());
    }

    private RuntimeService runTest(List<String> processIds, Map<String, ProcessInstance> processMap) throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable(eq(PROCESS_IDS))).thenReturn(processIds);

        ProcessVariablesCollector collector = new ProcessVariablesCollector();
        collector.install(execution);

        RuntimeService runtimeService = mockRuntimeService(processMap, "invalid");

        JavaDelegate delegate = new KillMachineSetUpProcesses(runtimeService, PROCESS_IDS);
        delegate.execute(execution);

        return runtimeService;
    }
}

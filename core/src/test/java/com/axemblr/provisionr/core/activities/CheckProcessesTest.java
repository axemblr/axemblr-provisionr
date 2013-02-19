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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;

public abstract class CheckProcessesTest {

    protected RuntimeService mockRuntimeService(Map<String, ProcessInstance> instances, 
                                              String... notFoundProcessInstanceIds) {
        RuntimeService runtimeService = mock(RuntimeService.class);

        ProcessInstanceQuery generalQuery = mock(ProcessInstanceQuery.class);
        for (Map.Entry<String, ProcessInstance> entry : instances.entrySet()) {
            ProcessInstanceQuery specificQuery = mock(ProcessInstanceQuery.class);
            when(specificQuery.singleResult()).thenReturn(entry.getValue());

            when(generalQuery.processInstanceId(eq(entry.getKey()))).thenReturn(specificQuery);
        }
        for (String notFound : notFoundProcessInstanceIds) {
            /* create a mock that returns null for all method calls (default) */
            when(generalQuery.processInstanceId(eq(notFound))).thenReturn(mock(ProcessInstanceQuery.class));
        }
        when(runtimeService.createProcessInstanceQuery()).thenReturn(generalQuery);

        return runtimeService;
    }

    protected ProcessInstance mockProcessInstance(boolean ended) {
        ProcessInstance processInstance = mock(ProcessInstance.class);
        when(processInstance.isEnded()).thenReturn(ended);
        return processInstance;
    }
}

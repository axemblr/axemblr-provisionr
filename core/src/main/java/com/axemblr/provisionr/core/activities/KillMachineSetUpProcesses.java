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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KillMachineSetUpProcesses implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(KillMachineSetUpProcesses.class);

    private RuntimeService runtimeService;
    private String variableWithProcessIds;

    public KillMachineSetUpProcesses(RuntimeService runtimeService, String variableWithProcessIds) {
        this.runtimeService = checkNotNull(runtimeService, "runtimeService is null");
        this.variableWithProcessIds = checkNotNull(variableWithProcessIds, "variableWithProcessIds is null");
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        @SuppressWarnings("unchecked")
        List<String> processIds = (List<String>) execution.getVariable(variableWithProcessIds);

        List<String> forceEnded = Lists.newArrayList(Iterables.filter(processIds, new Predicate<String>() {
            @Override
            public boolean apply(String processInstanceId) {
                ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
                if (instance != null && !instance.isEnded()) {
                    runtimeService.deleteProcessInstance(processInstanceId, "Pending process needs to be killed");
                    return true;
                }
                return false;
            }
        }));
        LOG.info("Killed pending machine setup processes: {}", forceEnded);
    }
}

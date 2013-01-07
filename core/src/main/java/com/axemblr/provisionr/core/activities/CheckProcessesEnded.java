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
import java.util.List;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckProcessesEnded implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CheckProcessesEnded.class);

    private final RuntimeService runtimeService;
    private final String variableWithProcessIds;
    private final String resultVariable;

    public CheckProcessesEnded(RuntimeService runtimeService, String variableWithProcessIds,
                               String resultVariable) {
        this.runtimeService = checkNotNull(runtimeService, "runtimeService is null");
        this.variableWithProcessIds = checkNotNull(variableWithProcessIds, "variableWithProcessIds is null");
        this.resultVariable = checkNotNull(resultVariable, "resultVariable is null");
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        @SuppressWarnings("unchecked")
        List<String> processIds = (List<String>) execution.getVariable(variableWithProcessIds);

        final boolean allDone = Iterables.all(processIds, new Predicate<String>() {
            @Override
            public boolean apply(String instanceId) {
                ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(instanceId).singleResult();

                return instance == null || instance.isEnded();
            }
        });

        LOG.info("CheckProcessesEnded {}: {}", processIds, allDone);
        execution.setVariable(resultVariable, allDone);
    }
}

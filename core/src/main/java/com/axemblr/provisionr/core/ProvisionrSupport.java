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

package com.axemblr.provisionr.core;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Optional;
import java.util.List;
import java.util.NoSuchElementException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProvisionrSupport implements Provisionr {

    private static final Logger LOG = LoggerFactory.getLogger(ProvisionrSupport.class);

    @Override
    public Optional<Provider> getDefaultProvider() {
        return Optional.absent();
    }

    /**
     * Trigger a signal event on all executions for a specific business key
     */
    protected void triggerSignalEvent(ProcessEngine processEngine, String businessKey, String signalName) {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceBusinessKey(businessKey).singleResult();

        List<Execution> executions = runtimeService.createExecutionQuery()
            .processInstanceId(processInstance.getProcessInstanceId())
            .signalEventSubscriptionName(signalName).list();

        if (executions.isEmpty()) {
            throw new NoSuchElementException(String.format("No executions found waiting " +
                "for signal '%s' on process %s", signalName, businessKey));
        }
        for (Execution execution : executions) {
            LOG.info("Sending '{}' signal to execution {} for process {}",
                new Object[]{signalName, execution.getId(), businessKey});
            runtimeService.signalEventReceived(signalName, execution.getId());

        }
    }
}

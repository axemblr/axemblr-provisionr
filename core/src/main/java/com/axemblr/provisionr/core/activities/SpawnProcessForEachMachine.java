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

import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreConstants;
import com.axemblr.provisionr.core.CoreProcessVariables;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create an Activiti process for each machine and store the process IDs
 */
public class SpawnProcessForEachMachine implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SpawnProcessForEachMachine.class);

    private final ProcessEngine processEngine;
    private final String processKey;
    private final String type;
    private final String resultVariable;

    public SpawnProcessForEachMachine(
        ProcessEngine processEngine, String processKey, String type, String resultVariable
    ) {
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
        this.processKey = checkNotNull(processKey, "processKey is null");
        this.type = checkNotNull(type, "type is null");
        this.resultVariable = checkNotNull(resultVariable, "resultVariable is null");
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final Pool pool = (Pool) execution.getVariable(CoreProcessVariables.POOL);

        @SuppressWarnings("unchecked")
        List<Machine> machines = (List<Machine>) execution.getVariable(CoreProcessVariables.MACHINES);
        checkNotNull(machines, "expecting to find the list of machines as process variable");

        /* Authenticate as kermit to make the process visible in the Explorer UI */
        processEngine.getIdentityService().setAuthenticatedUserId(CoreConstants.ACTIVITI_EXPLORER_DEFAULT_USER);

        List<String> processIds = Lists.newArrayList();
        for (Machine machine : machines) {
            final String businessKey = String.format("%s-%s-%s",
                execution.getProcessBusinessKey(), type, machine.getExternalId());

            ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceByKey(
                processKey, businessKey,
                ImmutableMap.<String, Object>of(CoreProcessVariables.POOL, pool, "machine", machine));

            LOG.info("Started background '" + type + "' process {} ({}) for machine {}",
                new Object[]{businessKey, instance.getId(), machine.getExternalId()});
            processIds.add(instance.getId());
        }

        LOG.info("Saving process IDs {} as {}", processIds, resultVariable);
        execution.setVariable(resultVariable, processIds);
    }
}

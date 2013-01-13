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

package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.ProcessVariables;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.jclouds.cloudstack.CloudStackClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a list of ID's, one for each machine of the pool and publishes it as a process variable.
 * This activity also registers the GATEWAY machine.
 */
public class GenerateListOfMachineIds extends CloudStackActivity {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateListOfMachineIds.class);
    private final String machineIdFormat = "host-%s-%03d";

    @Override
    public void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution) {
        List<String> machineIds = (List<String>) execution.getVariable(ProcessVariables.GENERATED_MACHINE_IDS);
        if (machineIds == null) {
            LOG.info("Process variable {} is empty, generating {} ids", ProcessVariables.GENERATED_MACHINE_IDS,
                pool.getExpectedSize());

            machineIds = generateIdsFromBusinessKey(execution.getProcessBusinessKey(), pool.getExpectedSize());
            LOG.debug("Update process variable {} to {}", ProcessVariables.GENERATED_MACHINE_IDS, machineIds);
            execution.setVariable(ProcessVariables.GENERATED_MACHINE_IDS, machineIds);
            final String gateway = machineIds.get(0);
            LOG.info("Setting gateway machine with id {}", gateway);
            execution.setVariable(CoreProcessVariables.GATEWAY, gateway);
        } else {
            LOG.info("Not updating process variable {}. Existing value is {} ", ProcessVariables.GENERATED_MACHINE_IDS,
                machineIds);
            LOG.info("Gateway is {}", execution.getVariable(CoreProcessVariables.GATEWAY));
        }
    }

    @VisibleForTesting
    protected List<String> generateIdsFromBusinessKey(String processBusinessKey, int expectedSize) {
        List<String> machineIds = Lists.newArrayList();
        for (int i = 1; i <= expectedSize; i++) {
            machineIds.add(String.format(machineIdFormat, processBusinessKey, i));
        }
        return machineIds;
    }
}

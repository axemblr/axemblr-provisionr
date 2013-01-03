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

package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.ProviderOptions;
import com.axemblr.provisionr.cloudstack.core.KeyPairs;
import com.axemblr.provisionr.cloudstack.core.Networks;
import com.axemblr.provisionr.cloudstack.core.VirtualMachines;
import org.activiti.engine.delegate.DelegateExecution;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunInstances extends CloudStackActivity {

    public static final Logger LOG = LoggerFactory.getLogger(RunInstances.class);

    @Override
    public void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution) {
        final String businessKey = execution.getProcessBusinessKey();

        final String keyPairName = KeyPairs.formatNameFromBusinessKey(businessKey);

        final String zoneId = pool.getOptions().get(ProviderOptions.ZONE_ID);
        final String templateId = pool.getSoftware().getBaseOperatingSystem();
        final String serviceOffering = pool.getHardware().getType();

        LOG.info("Starting instances!");

        AsyncCreateResponse asyncCreateResponse = cloudStackClient.getVirtualMachineClient()
            .deployVirtualMachineInZone(zoneId, serviceOffering, templateId,
                DeployVirtualMachineOptions.Builder
                    .displayName(businessKey)
                    .group(businessKey)
                    .networkId(Networks.formatNameFromBusinessKey(businessKey))
                    .keyPair(keyPairName)
                    .name(businessKey));

        VirtualMachines.waitForVMtoStart(cloudStackClient, businessKey);
    }
}

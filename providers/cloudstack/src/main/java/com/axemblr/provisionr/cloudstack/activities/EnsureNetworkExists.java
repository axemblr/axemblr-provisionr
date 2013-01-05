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
import com.axemblr.provisionr.cloudstack.NetworkOptions;
import com.axemblr.provisionr.cloudstack.ProcessVariables;
import com.axemblr.provisionr.cloudstack.ProviderOptions;
import com.axemblr.provisionr.cloudstack.core.Networks;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.NoSuchElementException;
import org.activiti.engine.delegate.DelegateExecution;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.options.CreateNetworkOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureNetworkExists extends CloudStackActivity {

    private static final Logger LOG = LoggerFactory.getLogger(EnsureNetworkExists.class);

    @Override
    public void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution) {
        if (execution.getVariable(ProcessVariables.NETWORK_ID) != null) {
            LOG.warn("Network process variable ({}) will be overwritten!", ProcessVariables.NETWORK_ID);
        }
        Network network;
        final String existingNetwork = pool.getNetwork().getOption(NetworkOptions.EXISTING_NETWORK_ID);
        if (existingNetwork != null) {
            network = checkNotNull(cloudStackClient.getNetworkClient().getNetwork(existingNetwork),
                "Network with id " + existingNetwork + " does not exist");
        } else {
            final String networkName = Networks.formatNameFromBusinessKey(execution.getProcessBusinessKey());
            final String zoneId = pool.getProvider().getOption(ProviderOptions.ZONE_ID);
            final String networkOffering = pool.getProvider().getOption(ProviderOptions.NETWORK_OFFERING);
            try {
                network = Networks.getByName(cloudStackClient, networkName);
                LOG.info("Network with name {} exists.", networkName);
            } catch (NoSuchElementException e) {
                LOG.info(String.format("Creating network %s in zone %s with offering %s", networkName, zoneId, networkOffering));
                network = cloudStackClient.getNetworkClient().createNetworkInZone(zoneId, networkOffering, networkName,
                    networkName, CreateNetworkOptions.NONE);
            }
        }
        LOG.info("Storing network id {} in process variable {}", network.getId(), ProcessVariables.NETWORK_ID);
        execution.setVariable(ProcessVariables.NETWORK_ID, network.getId());
    }
}

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

package com.axemblr.provisionr.amazon.activities;

import static com.google.common.base.Preconditions.checkNotNull;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.base.Optional;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Terminate instances previously started by {@see RunOnDemandInstances}
 */
public class TerminateInstances extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(TerminateInstances.class);

    public TerminateInstances(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        @SuppressWarnings("unchecked")
        Optional<List<String>> instanceIds = 
                Optional.fromNullable((List<String>) execution.getVariable(ProcessVariables.INSTANCE_IDS));

        LOG.info(">> Terminating instances: {}", instanceIds);
        if (instanceIds.isPresent() && instanceIds.get().size() > 0) {
            client.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instanceIds.get()));
        }
    }
}

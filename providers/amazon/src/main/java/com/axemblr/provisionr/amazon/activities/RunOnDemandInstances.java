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

import java.io.IOException;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class RunOnDemandInstances extends RunInstances {

    private static final Logger LOG = LoggerFactory.getLogger(RunOnDemandInstances.class);

    public static final String DEFAULT_ARCH = "amd64";
    public static final String DEFAULT_TYPE = "instance-store";

    public RunOnDemandInstances(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws IOException {

        final RunInstancesRequest request = createOnDemandInstancesRequest(pool, execution);
        // TODO allow for more options (e.g. monitoring & termination protection etc.)

        LOG.info(">> Sending RunInstances request: {}", request);
        RunInstancesResult result = client.runInstances(request);
        LOG.info("<< Got RunInstances result: {}", result);

        // TODO tag instances: managed-by: Axemblr Provisionr, business-key: ID etc.

        execution.setVariable(ProcessVariables.RESERVATION_ID,
            result.getReservation().getReservationId());
        execution.setVariable(ProcessVariables.INSTANCE_IDS,
            collectInstanceIdsAsList(result.getReservation().getInstances()));
    }


    private List<String> collectInstanceIdsAsList(List<Instance> instances) {
        /* Make a copy as an ArrayList to force lazy collection evaluation */
        return Lists.newArrayList(Lists.transform(instances,
            new Function<Instance, String>() {
                @Override
                public String apply(Instance instance) {
                    return instance.getInstanceId();
                }
            }));
    }
}

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

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use the IDs to retrieve details about the running machines and
 * store them as a process variable (machines)
 *
 * @see Machine
 */
public class PublishListOfMachines extends AmazonActivity {

    public static final Logger LOG = LoggerFactory.getLogger(PublishListOfMachines.class);

    public PublishListOfMachines(ProviderClientCache providerClientCache) {
        super(providerClientCache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        @SuppressWarnings("unchecked")
        List<String> instanceIds = (List<String>) execution.getVariable(ProcessVariables.INSTANCE_IDS);
        checkNotNull(instanceIds, "%s not found as a process variable", ProcessVariables.INSTANCE_IDS);

        LOG.info(">> Describing instances {}", instanceIds);
        DescribeInstancesResult result = client.describeInstances(new DescribeInstancesRequest()
            .withInstanceIds(instanceIds));

        LOG.info("<< Got the following reservations: {}", result.getReservations());

        List<Instance> instances = collectInstancesFromReservations(result.getReservations());
        List<Machine> machines = Lists.transform(instances,
            new Function<Instance, Machine>() {
                @Override
                public Machine apply(Instance instance) {
                    return Machine.builder()
                        .externalId(instance.getInstanceId())
                        .publicDnsName(instance.getPublicDnsName())
                        .publicIp(instance.getPublicIpAddress())
                        .privateDnsName(instance.getPrivateDnsName())
                        .privateIp(instance.getPrivateIpAddress())
                        .createMachine();
                }
            });

        /* Create a new ArrayList to force evaluation for lazy collections */
        execution.setVariable(CoreProcessVariables.MACHINES, Lists.newArrayList(machines));
    }
}

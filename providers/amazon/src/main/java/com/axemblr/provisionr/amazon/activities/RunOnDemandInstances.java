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
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.options.ProviderOptions;
import com.axemblr.provisionr.amazon.options.SoftwareOptions;
import com.axemblr.provisionr.amazon.core.ImageTable;
import com.axemblr.provisionr.amazon.core.ImageTableQuery;
import com.axemblr.provisionr.amazon.core.KeyPairs;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.amazon.core.SecurityGroups;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.VariableScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunOnDemandInstances extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(RunOnDemandInstances.class);

    public static final String DEFAULT_ARCH = "amd64";
    public static final String DEFAULT_TYPE = "instance-store";

    public RunOnDemandInstances(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        final String businessKey = execution.getProcessBusinessKey();

        final String securityGroupName = SecurityGroups.formatNameFromBusinessKey(businessKey);
        final String keyPairName = KeyPairs.formatNameFromBusinessKey(businessKey);

        final String instanceType = pool.getHardware().getType();
        final String imageId = getImageIdFromProcessVariablesOrQueryImageTable(
            execution, pool.getProvider(), instanceType);

        final RunInstancesRequest request = new RunInstancesRequest()
            .withClientToken(businessKey)
            .withSecurityGroups(securityGroupName)
            .withKeyName(keyPairName)
            .withInstanceType(instanceType)
            .withImageId(imageId)
            .withMinCount(pool.getMinSize())
            .withMaxCount(pool.getExpectedSize());

        // TODO allow for more options (e.g. monitoring & termination protection etc.)

        LOG.info(">> Sending RunInstances request: {}", request);
        RunInstancesResult result = client.runInstances(request);
        LOG.info("<< Got RunInstances result: {}", result);

        // TODO tag instances: managed-by: Axemblr Provisionr, business-key: ID etc.

        execution.setVariable(ProcessVariables.RESERVATION_ID,
            result.getReservation().getReservationId());
        execution.setVariable(ProcessVariables.INSTANCE_IDS,
            collectInstanceIdsAsArray(result.getReservation().getInstances()));
    }

    private String getImageIdFromProcessVariablesOrQueryImageTable(
        VariableScope execution, Provider provider, String instanceType
    ) {
        final String imageId = (String) execution.getVariable(ProcessVariables.CACHED_IMAGE_ID);
        if (imageId != null) {
            return imageId;
        }

        ImageTable imageTable;
        try {
            imageTable = ImageTable.fromCsvResource("/com/axemblr/provisionr/amazon/ubuntu.csv");
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        final String region = provider.getOptionOr(ProviderOptions.REGION, ProviderOptions.DEFAULT_REGION);
        final String version = provider.getOptionOr(SoftwareOptions.BASE_OPERATING_SYSTEM_VERSION,
            SoftwareOptions.DEFAULT_BASE_OPERATING_SYSTEM_VERSION);

        ImageTableQuery query = imageTable.query()
            .filterBy("region", region)
            .filterBy("version", version)
            .filterBy("arch", DEFAULT_ARCH);

        if (instanceType.equals("t1.micro")) {
            query.filterBy("type", "ebs");
        } else {
            query.filterBy("type", DEFAULT_TYPE);
        }

        return query.singleResult();
    }

    private String[] collectInstanceIdsAsArray(List<Instance> instances) {
        List<String> ids = Lists.transform(instances,
            new Function<Instance, String>() {
                @Override
                public String apply(Instance instance) {
                    return instance.getInstanceId();
                }
            });

        return ids.toArray(new String[ids.size()]);
    }
}

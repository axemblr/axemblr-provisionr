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

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AllInstancesMatchPredicate extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(AllInstancesMatchPredicate.class);

    private final String resultVariable;
    private final Predicate<Instance> predicate;

    protected AllInstancesMatchPredicate(ProviderClientCache cache,
                                         String resultVariable, Predicate<Instance> predicate) {
        super(cache);
        this.resultVariable = checkNotNull(resultVariable, "resultVariable is null");
        this.predicate = checkNotNull(predicate, "predicate is null");
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        @SuppressWarnings("unchecked")
        List<String> instanceIds = (List<String>) execution.getVariable(ProcessVariables.INSTANCE_IDS);
        checkNotNull(instanceIds, "process variable '{}' not found", ProcessVariables.INSTANCE_IDS);

        DescribeInstancesResult result = client.describeInstances(new DescribeInstancesRequest()
            .withInstanceIds(instanceIds));
        checkState(result.getReservations().size() == 1, "the instance ids are part of multiple reservations");

        try {
            List<Instance> instances = result.getReservations().get(0).getInstances();
            if (Iterables.all(instances, predicate)) {
                LOG.info(">> All {} instances match predicate {} ", instanceIds, predicate);
                execution.setVariable(resultVariable, true);

            } else {
                LOG.info("<< Not all instances {} match predicate {}", instanceIds, predicate);
                execution.setVariable(resultVariable, false);
            }
        } catch (AmazonServiceException exception) {
            if (exception.getErrorCode().equals("InvalidInstanceID.NotFound")) {
                LOG.warn("<< Got error InvalidInstanceID.NotFound. Assuming predicate {} is false", predicate);
                execution.setVariable(resultVariable, false);
            }
        }
    }
}

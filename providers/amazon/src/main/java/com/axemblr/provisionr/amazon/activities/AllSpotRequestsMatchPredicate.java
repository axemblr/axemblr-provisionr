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

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

public class AllSpotRequestsMatchPredicate extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(AllSpotRequestsMatchPredicate.class);

    protected final String resultVariable;
    private final Predicate<SpotInstanceRequest> predicate;

    protected AllSpotRequestsMatchPredicate(ProviderClientCache cache, String resultVariable, 
            Predicate<SpotInstanceRequest> predicate) {
        super(cache);
        this.resultVariable = checkNotNull(resultVariable, "resultVariable is null");
        this.predicate = checkNotNull(predicate, "predicate is null");
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {

        LOG.info(">> Checking if all spot requests match predicate {}", predicate);

        @SuppressWarnings("unchecked")
        List<String> requestIds = (List<String>) execution.getVariable(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS);
        checkNotNull(requestIds, "process variable '{}' not found", ProcessVariables.SPOT_INSTANCE_REQUEST_IDS);

        DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest();
        describeRequest.setSpotInstanceRequestIds(requestIds);

        try {
            // Retrieve all of the requests we want to monitor.
            DescribeSpotInstanceRequestsResult describeResult = client.describeSpotInstanceRequests(describeRequest);
            List<SpotInstanceRequest> requests = describeResult.getSpotInstanceRequests();

            if (Iterables.all(requests, predicate)) {
                LOG.info(">> All {} requests match predicate {} ", requests, predicate);
                execution.setVariable(resultVariable, true);
            } else {
                LOG.info("<< Not all requests {} match predicate {}", requests, predicate);
                execution.setVariable(resultVariable, false);
            }
        } catch (AmazonServiceException exception) {
            // couldn't find relevant error codes, so we always propagate the exception
            throw Throwables.propagate(exception);
        }
    }
}

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
import java.util.concurrent.TimeUnit;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.RequestSpotInstancesResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;


public class RunSpotInstances extends RunInstances {

    private static final Logger LOG = LoggerFactory.getLogger(RunSpotInstances.class);
    
    public RunSpotInstances(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        /* before sending a new request, we check to see if we already registered
           a launch group with the process ID, if yes, we don't re-send the request */
        final String businessKey = execution.getProcessBusinessKey();

        /* we timeout if requests have already been sent - the activity is being retried. */
        Optional<Object> alreadySent = Optional.fromNullable(
                execution.getVariable(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS));

        if (alreadySent.isPresent()) {
            DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest()
                    .withFilters(new Filter()
                        .withName("launch-group").withValues(businessKey)
                        .withName("state").withValues("open", "active"));
            Stopwatch stopwatch = new Stopwatch().start();
            while (stopwatch.elapsedTime(TimeUnit.MINUTES) < 2) {
                DescribeSpotInstanceRequestsResult result = client.describeSpotInstanceRequests(describeRequest);
                List<SpotInstanceRequest> pending = result.getSpotInstanceRequests();
                if (pending.size() > 0) {
                    LOG.info("Not resending spot instance requests {} for businessKey: {}.", pending, businessKey);
                    execution.setVariable(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS, 
                            collectSpotInstanceRequestIds(pending));
                    return;
                }
                LOG.info("The describe call has not returned anything yet, waiting 20s and retrying.");
                Uninterruptibles.sleepUninterruptibly(20, TimeUnit.SECONDS);
            }
        }

        final RequestSpotInstancesRequest request = createSpotInstancesRequest(pool, execution);
        execution.setVariable(ProcessVariables.SPOT_REQUESTS_SENT, true);
        RequestSpotInstancesResult requestResult = client.requestSpotInstances(request);
        List<String> spotInstanceRequestIds = collectSpotInstanceRequestIds(requestResult.getSpotInstanceRequests());

        execution.setVariable(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS, spotInstanceRequestIds);
    }
    
    private List<String> collectSpotInstanceRequestIds(List<SpotInstanceRequest> requestResponses) {
        /* Make a copy as an ArrayList to force lazy collection evaluation */
        return Lists.newArrayList(Lists.transform(requestResponses,
            new Function<SpotInstanceRequest, String>() {
                @Override
                public String apply(SpotInstanceRequest instanceRequest) {
                    return instanceRequest.getSpotInstanceRequestId();
                }
            }));
    }
}

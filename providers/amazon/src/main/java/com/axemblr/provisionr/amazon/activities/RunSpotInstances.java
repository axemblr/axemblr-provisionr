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

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.RequestSpotInstancesResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.base.Function;
import com.google.common.collect.Lists;


public class RunSpotInstances extends RunInstances {

    public RunSpotInstances(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
    	final RequestSpotInstancesRequest request = createSpotInstancesRequest(pool, execution);
    	
    	RequestSpotInstancesResult requestResult = client.requestSpotInstances(request);
    	
        execution.setVariable(ProcessVariables.INSTANCE_IDS,
        		collectSpotInstanceRequestIds(requestResult.getSpotInstanceRequests()));
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

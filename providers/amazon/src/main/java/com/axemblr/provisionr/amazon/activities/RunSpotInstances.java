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

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
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
    	
    	List<String> spotInstanceRequestIds = 
    			collectSpotInstanceRequestIds(requestResult.getSpotInstanceRequests());
    	
    	// Create a variable that will track whether there are any
    	// requests still in the open state.
    	boolean anyOpen;
    	List<String> instanceIds = new ArrayList<String>();
    	do {
    	    // Create the describeRequest object with all of the request ids
    	    // to monitor (e.g. that we started).
    	    DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest();
    	    describeRequest.setSpotInstanceRequestIds(spotInstanceRequestIds);

    	    // Initialize the anyOpen variable to false - which assumes there
    	    // are no requests open unless we find one that is still open.
    	    anyOpen = false;

    	    try {
    	        // Retrieve all of the requests we want to monitor.
    	        DescribeSpotInstanceRequestsResult describeResult = client.describeSpotInstanceRequests(describeRequest);
    	        List<SpotInstanceRequest> describeResponses = describeResult.getSpotInstanceRequests();

    	        // Look through each request and determine if they are all in
    	        // the active state.
    	        for (SpotInstanceRequest describeResponse : describeResponses) {
	    	        if (describeResponse.getState().equals("open")) {
	    	            anyOpen = true;
	    	            break;
	    	        }
	    	        // Add the instance id to the list we will
	    	        // eventually terminate.
	    	        if (describeResponse.getState().equals("active")) {
	    	        	instanceIds.add(describeResponse.getInstanceId());
	    	        }
    	        }
    	    } catch (AmazonServiceException e) {
				// If we have an exception, ensure we don't break out of
				// the loop. This prevents the scenario where there was
				// blip on the wire.
				anyOpen = true;
    	    }
    	    
    	    // TODO: check that this timeout is ok
    	    try {
    	        // Sleep for 60 seconds.
    	        Thread.sleep(60*1000);
    	    } catch (Exception e) {
    	        // Do nothing because it woke up early.
    	    }
    	} while (anyOpen);
    	
    	
        execution.setVariable(ProcessVariables.INSTANCE_IDS, instanceIds);
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

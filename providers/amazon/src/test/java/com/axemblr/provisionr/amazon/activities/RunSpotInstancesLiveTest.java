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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.options.ProviderOptions;
import com.axemblr.provisionr.test.ProcessVariablesCollector;
import com.google.common.util.concurrent.Uninterruptibles;

public class RunSpotInstancesLiveTest extends RunInstancesLiveTest<RunSpotInstances> {

    /**
     * This should be set a bit higher than the on demand instance
     * price to avoid the situation in which the test fails because
     * the spot bid is too low. 
     */
    public static String AMAZON_SPOT_BID = "0.04";

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final String region = getProviderProperty(ProviderOptions.REGION, ProviderOptions.DEFAULT_REGION);
        provider = collectProviderCredentialsFromSystemProperties()
                .option(ProviderOptions.REGION, region)
                .option(ProviderOptions.SPOT_BID, AMAZON_SPOT_BID)
                .createProvider();
        when(pool.getProvider()).thenReturn(provider);
    }

    @Test
    public void testRunSpotInstances() throws Exception {
        ProcessVariablesCollector collector = new ProcessVariablesCollector();
        collector.install(execution);

        activity.execute(execution);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> argument = (ArgumentCaptor<List<String>>) 
                (Object) ArgumentCaptor.forClass(List.class);
        verify(execution).setVariable(eq(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS), argument.capture());
        when(execution.getVariable(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS)).thenReturn(argument.getValue());
        /* The timeout is needed because the describe calls don't return immediately. */
        // TODO: see if we can eliminate this after adding the process variables conditions
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MINUTES);

        // shouldn't do anything
        activity.execute(execution);

        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MINUTES);

        DescribeSpotInstanceRequestsResult result = client.describeSpotInstanceRequests(
                new DescribeSpotInstanceRequestsRequest().withFilters(new Filter()
                    .withName("launch-group").withValues(BUSINESS_KEY)));

        assertThat(result.getSpotInstanceRequests()).hasSize(1);
        /* we also need to sleep before the teardown */
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.MINUTES);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void tearDown() throws Exception {
        // cleanup any pending requests or instances
        ArgumentCaptor<List<String>> argument = (ArgumentCaptor<List<String>>) 
                (Object) ArgumentCaptor.forClass(List.class);

        executeActivitiesInSequence(execution,
                CancelSpotRequests.class, 
                GetInstanceIdsFromSpotRequests.class);

        verify(execution).setVariable(eq(ProcessVariables.INSTANCE_IDS), argument.capture());
        when(execution.getVariable(ProcessVariables.INSTANCE_IDS)).thenReturn(argument.getValue());

        executeActivitiesInSequence(execution, TerminateInstances.class);
        super.tearDown();
    }

}

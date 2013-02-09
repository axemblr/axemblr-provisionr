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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.test.ProcessVariablesCollector;

public class RunOnDemandInstancesLiveTest extends RunInstancesLiveTest<RunOnDemandInstances> {

    @Test
    public void testRunInstances() throws Exception {
        ProcessVariablesCollector collector = new ProcessVariablesCollector();
        collector.install(execution);

        activity.execute(execution);

        verify(execution).setVariable(eq(ProcessVariables.RESERVATION_ID), anyString());
        verify(execution).setVariable(eq(ProcessVariables.INSTANCE_IDS), any());

        @SuppressWarnings("unchecked")
        List<String> instanceIds = (List<String>) collector.getVariable(ProcessVariables.INSTANCE_IDS);

        /* the second call should do nothing */
        activity.execute(execution);

        DescribeInstancesResult result = client.describeInstances(new DescribeInstancesRequest()
            .withInstanceIds(instanceIds));

        assertThat(result.getReservations()).hasSize(1);
        assertThat(result.getReservations().get(0).getInstances()).hasSize(1);

        client.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instanceIds));
    }
}

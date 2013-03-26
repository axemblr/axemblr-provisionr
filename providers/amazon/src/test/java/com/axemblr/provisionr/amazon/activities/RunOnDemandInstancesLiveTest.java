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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.Volume;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.api.hardware.BlockDevice;
import com.axemblr.provisionr.test.ProcessVariablesCollector;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class RunOnDemandInstancesLiveTest extends CreatePoolLiveTest<RunOnDemandInstances> {

    private static final String UBUNTU_AMI_ID = "ami-1e831d77"; // Ubuntu 13.04 amd64
    private ProcessVariablesCollector collector;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        collector = new ProcessVariablesCollector();
        collector.install(execution);
    }

    @Test
    public void testRunInstances() throws Exception {

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

    }

    @Test
    public void testRunInstancesWithBlockDevices() throws Exception {
        // TODO: maybe we should also test for spot instances
        BlockDevice blockDevice = mock(BlockDevice.class);
        when(blockDevice.getSize()).thenReturn(8); // TODO: understand why it doesn't work for smaller volumes
        when(blockDevice.getName()).thenReturn("/dev/sda1");
        BlockDevice blockDevice2 = mock(BlockDevice.class);
        when(blockDevice2.getSize()).thenReturn(16);
        when(blockDevice2.getName()).thenReturn("/dev/sda4");
        when(hardware.getBlockDevices()).thenReturn(Lists.newArrayList(blockDevice, blockDevice2));

        activity.execute(execution);

        Uninterruptibles.sleepUninterruptibly(30, TimeUnit.SECONDS);

        @SuppressWarnings("unchecked")
        List<String> instanceIds = (List<String>) collector.getVariable(ProcessVariables.INSTANCE_IDS);
        DescribeInstancesResult result = client.describeInstances(new DescribeInstancesRequest()
            .withInstanceIds(instanceIds));

        Instance instance = result.getReservations().get(0).getInstances().get(0);
        List<InstanceBlockDeviceMapping> bdm = instance.getBlockDeviceMappings();
        assertThat(bdm).hasSize(2);
        List<String> volumeIds = Lists.newArrayList();
        for (int i = 0; i < bdm.size(); i++) {
            assertThat(bdm.get(i).getDeviceName()).isEqualTo("/dev/sda" + ((i+1) * (i+1)));
            assertThat(bdm.get(i).getEbs().getDeleteOnTermination()).isTrue();
            volumeIds.add(bdm.get(i).getEbs().getVolumeId());
        }

        DescribeVolumesResult volumesResult = client.describeVolumes(
                new DescribeVolumesRequest().withVolumeIds(volumeIds));
        for (Volume volume : volumesResult.getVolumes()) {
            assertThat(volume.getState()).isIn(Lists.newArrayList("creating", "available", "in-use"));
        }
        assertThat(volumesResult.getVolumes().get(0).getSize()).isNotEqualTo(volumesResult.getVolumes().get(1).getSize());
    }

    @Test
    public void testRunInstancesWithABaseImageId() throws Exception {
        when(software.getImageId()).thenReturn(UBUNTU_AMI_ID);
        when(pool.getSoftware()).thenReturn(software);
        activity.execute(execution);

        @SuppressWarnings("unchecked")
        List<String> instanceIds = (List<String>) collector.getVariable(ProcessVariables.INSTANCE_IDS);
        DescribeInstancesResult result = client.describeInstances(new DescribeInstancesRequest()
            .withInstanceIds(instanceIds));

        Instance instance = result.getReservations().get(0).getInstances().get(0);
        assertThat(instance.getImageId()).isEqualTo(UBUNTU_AMI_ID);
    }

    @Override
    public void tearDown() throws Exception {
        @SuppressWarnings("unchecked")
        List<String> instanceIds = (List<String>) collector.getVariable(ProcessVariables.INSTANCE_IDS);
        client.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instanceIds));

        // TODO: this behaves strangely, if it tries to delete the security group 
        // and the instances are not yet killed, the test fails with an error
        Uninterruptibles.sleepUninterruptibly(45, TimeUnit.SECONDS);
        super.tearDown();
    }
}

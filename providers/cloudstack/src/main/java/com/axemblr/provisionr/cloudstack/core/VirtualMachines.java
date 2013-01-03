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

package com.axemblr.provisionr.cloudstack.core;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.VirtualMachine;
import static org.jclouds.cloudstack.domain.VirtualMachine.State.STARTING;
import org.jclouds.cloudstack.options.ListVirtualMachinesOptions;
import static org.jclouds.util.Preconditions2.checkNotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualMachines {

    private static final Logger LOG = LoggerFactory.getLogger(VirtualMachines.class);
    public static int WAIT_TIME_OUT_IN_SECONDS = 180;
    public static final int BETWEEN_REQUESTS_SLEEP_TIME = WAIT_TIME_OUT_IN_SECONDS / 10;

    private VirtualMachines() {
    }

    public static List<String> destroyAllVirtualMachineByName(final CloudStackClient client, final String vmName) {
        checkNotEmpty(vmName);

        Set<VirtualMachine> vms = Sets.filter(client.getVirtualMachineClient()
            .listVirtualMachines(ListVirtualMachinesOptions.Builder.name(vmName)), new Predicate<VirtualMachine>() {
            @Override
            public boolean apply(VirtualMachine input) {
                return vmName.equals(input.getDisplayName());
            }
        });

        List<String> jobIds = Lists.newArrayList();
        LOG.info("Deleting a total of {} virtual machine instances", vms.size());
        for (VirtualMachine vm : vms) {
            LOG.info("Deleting instance with id {}", vm.getId());
            jobIds.add(client.getVirtualMachineClient().destroyVirtualMachine(vm.getId()));
        }
        return ImmutableList.copyOf(jobIds);
    }

    public static void waitForVMtoStart(final CloudStackClient client, final String vmName) {
        checkNotEmpty(vmName);
        VirtualMachine machine = getVirtualMachineByName(client, vmName);
        String id = machine.getId();
        long startTime = System.currentTimeMillis();
        while (machine.getState() == STARTING) {
            try {
                LOG.info("Waiting for VM {} - id {} to start", machine.getName(), machine.getId());
                TimeUnit.SECONDS.sleep(BETWEEN_REQUESTS_SLEEP_TIME);
                machine = client.getVirtualMachineClient().getVirtualMachine(id);
            } catch (InterruptedException e) {
                LOG.info("Interrupted while waiting for VM's to start");
                Throwables.propagateIfPossible(e);
            }
            if (timeOutExceeded(startTime)) {
                break;
            }
        }

        switch (machine.getState()) {
            case RUNNING:
                LOG.info("VM {} is running, as expected", vmName);
                break;
            default:
                throw new IllegalStateException("VM " + machine + "is not in RUNNING state");
        }
    }

    private static boolean timeOutExceeded(long startTime) {
        return System.currentTimeMillis() - startTime > (WAIT_TIME_OUT_IN_SECONDS * 1000);
    }

    private static VirtualMachine getVirtualMachineByName(CloudStackClient client, final String vmName) {
        Set<VirtualMachine> machines = Sets.filter(client.getVirtualMachineClient()
            .listVirtualMachines(ListVirtualMachinesOptions.Builder.name(vmName)), new Predicate<VirtualMachine>() {
            @Override
            public boolean apply(VirtualMachine input) {
                return vmName.equals(input.getDisplayName());
            }
        });
        return Iterables.getOnlyElement(machines);
    }
}

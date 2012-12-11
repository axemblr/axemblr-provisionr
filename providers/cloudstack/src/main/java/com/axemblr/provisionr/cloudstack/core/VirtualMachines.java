package com.axemblr.provisionr.cloudstack.core;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.options.ListVirtualMachinesOptions;
import static org.jclouds.util.Preconditions2.checkNotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualMachines {

    private static final Logger LOG = LoggerFactory.getLogger(VirtualMachines.class);

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
}

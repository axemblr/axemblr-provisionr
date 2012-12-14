package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use the IDs to retrieve details about the running machines and
 * store them as a process variable (machines)
 *
 * @see Machine
 */
public class PublishArraysOfMachines extends AmazonActivity {

    public static final Logger LOG = LoggerFactory.getLogger(PublishArraysOfMachines.class);

    public PublishArraysOfMachines(ProviderClientCache providerClientCache) {
        super(providerClientCache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        String[] instanceIds = (String[]) execution.getVariable(ProcessVariables.INSTANCE_IDS);
        checkNotNull(instanceIds, "%s not found as a process variable", ProcessVariables.INSTANCE_IDS);

        LOG.info(">> Describing instances {}", Arrays.toString(instanceIds));
        DescribeInstancesResult result = client.describeInstances(new DescribeInstancesRequest()
            .withInstanceIds(instanceIds));
        checkArgument(result.getReservations().size() == 1, "found more than one reservation");

        Reservation reservation = result.getReservations().get(0);
        LOG.info("<< Got one reservation with {} running instances", reservation.getInstances().size());

        List<Machine> machines = Lists.transform(reservation.getInstances(),
            new Function<Instance, Machine>() {
                @Override
                public Machine apply(Instance instance) {
                    return Machine.builder()
                        .externalId(instance.getInstanceId())
                        .publicDnsName(instance.getPublicDnsName())
                        .publicIp(instance.getPublicIpAddress())
                        .privateDnsName(instance.getPrivateDnsName())
                        .privateIp(instance.getPrivateIpAddress())
                        .createMachine();
                }
            });

        execution.setVariable(CoreProcessVariables.MACHINES,
            machines.toArray(new Machine[machines.size()]));
    }
}

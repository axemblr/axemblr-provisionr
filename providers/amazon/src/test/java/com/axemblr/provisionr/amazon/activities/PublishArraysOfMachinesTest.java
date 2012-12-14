package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.axemblr.provisionr.test.ProcessVariablesCollector;
import com.google.common.collect.Lists;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PublishArraysOfMachinesTest {

    @Test
    public void testPublishListOfMachines() throws Exception {
        AmazonEC2 client = mock(AmazonEC2.class);

        ProviderClientCache clientCache = mock(ProviderClientCache.class);
        when(clientCache.get(Matchers.<Provider>any())).thenReturn(client);

        DelegateExecution execution = mock(DelegateExecution.class);
        String[] instanceIds = new String[]{"i-123", "i-456"};
        when(execution.getVariable(ProcessVariables.INSTANCE_IDS)).thenReturn(instanceIds);

        when(client.describeInstances(Matchers.<DescribeInstancesRequest>any()))
            .thenReturn(new DescribeInstancesResult()
                .withReservations(new Reservation().withInstances(
                    new Instance().withInstanceId("i-123").withPublicDnsName("i1.amazonaws.com")
                        .withPublicIpAddress("1.2.3.4").withPrivateDnsName("i1.internal").withPrivateIpAddress("10.1.2.3"),
                    new Instance().withInstanceId("i-456").withPublicDnsName("i2.amazonaws.com")
                        .withPublicIpAddress("5.6.7.8").withPrivateDnsName("i2.internal").withPrivateIpAddress("10.4.5.6")
                )));

        ProcessVariablesCollector collector = new ProcessVariablesCollector();
        collector.install(execution);

        AmazonActivity activity = new PublishArraysOfMachines(clientCache);
        activity.execute(client, null /* not used */, execution);

        Machine[] machines = (Machine[]) collector.getVariable(CoreProcessVariables.MACHINES);

        assertThat(machines).hasSize(2);
        assertThat(machines[0].getPublicDnsName()).isEqualTo("i1.amazonaws.com");
    }
}

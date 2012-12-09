package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.axemblr.provisionr.test.ProcessVariablesCollector;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RunOnDemandInstancesLiveTest extends AmazonActivityLiveTest<RunOnDemandInstances> {

    private DelegateExecution execution;
    private Pool pool;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        execution = mock(DelegateExecution.class);
        pool = mock(Pool.class);

        final AdminAccess adminAccess = AdminAccess.builder()
            .username("admin")
            .publicKey(getResourceAsString("keys/test.pub"))
            .privateKey(getResourceAsString("keys/test"))
            .createAdminAccess();

        final Network network = Network.builder().addRules(
            Rule.builder().anySource().tcp().port(22).createRule()).createNetwork();

        final Hardware hardware = Hardware.builder().type("t1.micro").createHardware();

        when(pool.getProvider()).thenReturn(provider);
        when(pool.getAdminAccess()).thenReturn(adminAccess);
        when(pool.getNetwork()).thenReturn(network);

        when(pool.getMinSize()).thenReturn(1);
        when(pool.getExpectedSize()).thenReturn(1);

        when(pool.getHardware()).thenReturn(hardware);

        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);
        when(execution.getVariable(ProcessVariables.POOL)).thenReturn(pool);

        new EnsureSecurityGroupExists().execute(execution);
        new EnsureKeyPairExists().execute(execution);
    }

    @Override
    public void tearDown() throws Exception {
        new DeleteKeyPair().execute(execution);
        new DeleteSecurityGroup().execute(execution);

        super.tearDown();
    }

    @Test
    public void testRunInstances() throws Exception {
        ProcessVariablesCollector collector = new ProcessVariablesCollector();
        doAnswer(collector).when(execution).setVariable(Matchers.<String>any(), any());

        activity.execute(execution);

        verify(execution).setVariable(eq(ProcessVariables.RESERVATION_ID), anyString());
        verify(execution).setVariable(eq(ProcessVariables.INSTANCES), any());
        String[] instanceIds = (String[]) collector.getVariable(ProcessVariables.INSTANCES);

        /* the second call should do nothing */
        activity.execute(execution);

        DescribeInstancesResult result = client.describeInstances(new DescribeInstancesRequest()
            .withInstanceIds(instanceIds));

        assertThat(result.getReservations()).hasSize(1);
        assertThat(result.getReservations().get(0).getInstances()).hasSize(1);

        client.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instanceIds));
    }
}

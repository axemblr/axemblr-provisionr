package com.axemblr.provisionr.amazon.activities;

import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.test.ProcessVariablesCollector;
import org.activiti.engine.delegate.DelegateExecution;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TerminateInstancesLiveTest extends AmazonActivityLiveTest<TerminateInstances> {

    private DelegateExecution execution;
    private Pool pool;
    private ProcessVariablesCollector collector;

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

        collector = new ProcessVariablesCollector();
        doAnswer(collector).when(execution).setVariable(Matchers.<String>any(), any());

        new EnsureSecurityGroupExists().execute(execution);
        new EnsureKeyPairExists().execute(execution);
        new RunOnDemandInstances().execute(execution);
    }

    @Override
    public void tearDown() throws Exception {
        new DeleteKeyPair().execute(execution);
        new DeleteSecurityGroup().execute(execution);

        super.tearDown();
    }

    @Test
    public void testTerminateInstances() throws Exception {
        when(execution.getVariable(ProcessVariables.INSTANCES))
            .thenReturn(collector.getVariable(ProcessVariables.INSTANCES));

        activity.execute(execution);

        /* the second execution should do nothing */
        activity.execute(execution);
    }
}

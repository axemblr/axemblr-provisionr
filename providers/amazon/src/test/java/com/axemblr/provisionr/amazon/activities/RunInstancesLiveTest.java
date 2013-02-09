package com.axemblr.provisionr.amazon.activities;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.activiti.engine.delegate.DelegateExecution;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;

public abstract class RunInstancesLiveTest<T extends AmazonActivity> extends AmazonActivityLiveTest<T> {
    
	protected DelegateExecution execution;
    protected Pool pool;
	
	@Override
    @SuppressWarnings("unchecked")
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
        when(execution.getVariable(CoreProcessVariables.POOL)).thenReturn(pool);

        executeActivitiesInSequence(execution,
            EnsureKeyPairExists.class,
            EnsureSecurityGroupExists.class
        );
    }
	
    @Override
    @SuppressWarnings("unchecked")
    public void tearDown() throws Exception {
        executeActivitiesInSequence(execution,
            DeleteSecurityGroup.class,
            DeleteKeyPair.class
        );
        super.tearDown();
    }
}

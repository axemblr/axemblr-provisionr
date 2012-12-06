package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.pool.Pool;
import java.util.NoSuchElementException;
import org.activiti.engine.delegate.DelegateExecution;
import static org.junit.Assert.fail;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteSecurityGroupLiveTest extends CloudStackActivityLiveTest<DeleteSecurityGroup> {

    private final Logger LOG = LoggerFactory.getLogger(DeleteSecurityGroupLiveTest.class);
    private final String SECURITY_GROUP_NAME = SecurityGroups.formatSecurityGroupNameFromProcessBusinessKey(BUSINESS_KEY);

    @Test
    public void testDeleteSecurityGroup() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        final Network network = Network.builder().createNetwork();

        Pool pool = mock(Pool.class);

        when(pool.getProvider()).thenReturn(provider);
        when(pool.getNetwork()).thenReturn(network);

        when(execution.getVariable("pool")).thenReturn(pool);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);

        CreateSecurityGroup.createSecurityGroupWithRules(context.getApi(),
            network, SECURITY_GROUP_NAME);

        activity.execute(execution);

        try {
            SecurityGroups.getByName(context.getApi(), SECURITY_GROUP_NAME);
            fail("Does not throw Exception as it should have");
        } catch (NoSuchElementException e) {
            LOG.info("Exception thrown. Test passed.");
        }
    }
}

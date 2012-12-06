package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.software.Software;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import org.junit.After;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteSecurityGroupLiveTest extends AmazonActivityLiveTest<DeleteSecurityGroup> {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteSecurityGroupLiveTest.class);

    private final String SECURITY_GROUP_NAME = "network-" + BUSINESS_KEY;

    @After
    public void tearDown() {
        quietlyDeleteSecurityGroupIfExists(SECURITY_GROUP_NAME);
        super.tearDown();
    }

    @Test
    public void testDeleteSecurityGroup() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        Pool pool = mock(Pool.class);

        when(pool.getProvider()).thenReturn(provider);
        when(execution.getVariable("pool")).thenReturn(pool);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);

        client.createSecurityGroup(new CreateSecurityGroupRequest()
            .withGroupName(SECURITY_GROUP_NAME).withDescription("Just for test"));

        activity.execute(execution);

        try {
            client.describeSecurityGroups(new DescribeSecurityGroupsRequest()
                .withGroupNames(SECURITY_GROUP_NAME));
            fail("Did not throw AmazonServiceException as expected");

        } catch (AmazonServiceException e) {
            assertThat(e.getErrorCode()).isEqualTo("InvalidGroup.NotFound");
        }
    }
}

package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.functions.ConvertIngressRuleToRule;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.NoSuchElementException;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.jclouds.cloudstack.domain.SecurityGroup;
import static org.jclouds.cloudstack.options.ListSecurityGroupsOptions.Builder.named;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSecurityGroupLiveTest extends CloudStackActivityLiveTest<CreateSecurityGroup> {

    private static final Logger LOG = LoggerFactory.getLogger(CreateSecurityGroupLiveTest.class);

    private final String SECURITY_GROUP_NAME = "network-" + BUSINESS_KEY;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        deleteSecurityGroupIfExists();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        deleteSecurityGroupIfExists();
        super.tearDown();
    }

    private void deleteSecurityGroupIfExists() {
        try {
            SecurityGroup securityGroup = Iterables.getOnlyElement(context.getApi()
                .getSecurityGroupClient()
                .listSecurityGroups(named(SECURITY_GROUP_NAME)));

            context.getApi().getSecurityGroupClient().deleteSecurityGroup(securityGroup.getId());
        } catch (NoSuchElementException e) {
            LOG.info("Security group {} was not found", SECURITY_GROUP_NAME);
        } catch (Exception e2) {
            LOG.error("Exception deleting security group {}", e2);
        }
    }

    @Test
    public void testCreateSecurityGroup() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);

        final ImmutableSet<Rule> ingressRules = ImmutableSet.of(
            Rule.builder().anySource().icmp().createRule(),
            Rule.builder().anySource().tcp().port(22).createRule(),
            Rule.builder().anySource().udp().port(53).createRule());

        final Network network = Network.builder().ingress(ingressRules).createNetwork();
        final Pool pool = Pool.builder().provider(provider).network(network).createPool();

        when(execution.getVariable("pool")).thenReturn(pool);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);
        activity.execute(execution);
        assertSecurityGroupExistsWithRules(SecurityGroups.getByName(
            context.getApi(), SECURITY_GROUP_NAME), ingressRules);
    }

    private void assertSecurityGroupExistsWithRules(SecurityGroup securityGroup, ImmutableSet<Rule> ingressRules) {
        assertThat(ingressRules).containsAll(Iterables.transform(securityGroup.getIngressRules(),
            ConvertIngressRuleToRule.FUNCTION));
    }

}

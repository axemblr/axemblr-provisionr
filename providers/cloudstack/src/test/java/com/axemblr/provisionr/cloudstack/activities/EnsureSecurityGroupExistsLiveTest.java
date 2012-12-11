package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.core.ConvertIngressRuleToRule;
import com.axemblr.provisionr.cloudstack.ProcessVariables;
import com.axemblr.provisionr.cloudstack.core.SecurityGroups;
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

public class EnsureSecurityGroupExistsLiveTest extends CloudStackActivityLiveTest<EnsureSecurityGroupExists> {

    private static final Logger LOG = LoggerFactory.getLogger(EnsureSecurityGroupExistsLiveTest.class);

    private final String SECURITY_GROUP_NAME = "network-" + BUSINESS_KEY;

    private final ImmutableSet<Rule> ingressRules = ImmutableSet.of(
        Rule.builder().anySource().icmp().createRule(),
        Rule.builder().anySource().tcp().port(22).createRule(),
        Rule.builder().anySource().udp().port(53).createRule());

    private final Network network = Network.builder().ingress(ingressRules).createNetwork();

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        logSecurityGroupDetails();
        deleteSecurityGroupIfExists();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        deleteSecurityGroupIfExists();
        logSecurityGroupDetails();
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
        Pool pool = mock(Pool.class);

        when(pool.getProvider()).thenReturn(provider);
        when(pool.getNetwork()).thenReturn(network);

        when(execution.getVariable(ProcessVariables.POOL)).thenReturn(pool);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);

        activity.execute(execution);
        assertSecurityGroupExistsWithRules(SecurityGroups.getByName(
            context.getApi(), SECURITY_GROUP_NAME), ingressRules);
    }

    @Test
    public void testCreateSecurityGroupWithExistingSecurityGroup() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        Pool pool = mock(Pool.class);

        when(pool.getProvider()).thenReturn(provider);

        when(execution.getVariable(ProcessVariables.POOL)).thenReturn(pool);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);

        // create the SecurityGroup with an extra Network Rule, then call the activity
        when(pool.getNetwork()).thenReturn(network.toBuilder().addRules(
            Rule.builder().anySource().tcp().port(80).createRule()).createNetwork());

        activity.execute(execution);
        // call the process again with the old network rules and check the rules
        when(pool.getNetwork()).thenReturn(network);

        activity.execute(execution);

        assertSecurityGroupExistsWithRules(SecurityGroups.getByName(context.getApi(),
            SECURITY_GROUP_NAME), ingressRules);
    }

    private void assertSecurityGroupExistsWithRules(SecurityGroup securityGroup, ImmutableSet<Rule> ingressRules) {
        assertThat(ingressRules).containsAll(Iterables.transform(securityGroup.getIngressRules(),
            ConvertIngressRuleToRule.FUNCTION));
    }
}

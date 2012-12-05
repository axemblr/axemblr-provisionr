package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.cloudstack.CloudStackProvisionr;
import com.axemblr.provisionr.cloudstack.functions.ConvertIngressRuleToRule;
import com.axemblr.provisionr.test.ProvisionrLiveTestSupport;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.SecurityGroup;
import static org.jclouds.cloudstack.options.ListSecurityGroupsOptions.Builder.named;
import org.jclouds.rest.RestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSecurityGroupLiveTest extends ProvisionrLiveTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CreateSecurityGroupLiveTest.class);

    private final CloudStackActivity ACTIVITY = new CreateSecurityGroup();

    private final String BUSINESS_KEY = "j-" + UUID.randomUUID().toString();
    private final String SECURITY_GROUP_NAME = "network-" + BUSINESS_KEY;

    private Provider provider;
    private RestContext<CloudStackClient, CloudStackAsyncClient> context;

    public CreateSecurityGroupLiveTest() {
        super(CloudStackProvisionr.ID);
    }

    @Before
    public void setUp() throws Exception {
        provider = collectProviderCredentialsFromSystemProperties().createProvider();
        LOG.info("Using provider {}", provider);
        context = ACTIVITY.newCloudStackClient(provider);
        deleteSecurityGroupIfExists();
    }

    @After
    public void tearDown() throws Exception {
        deleteSecurityGroupIfExists();
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
        ACTIVITY.execute(execution);
        assertSecurityGroupExistsWithRules(CreateSecurityGroup.getSecurityGroupByName(
            context.getApi().getSecurityGroupClient(), SECURITY_GROUP_NAME), ingressRules);
    }

    private void assertSecurityGroupExistsWithRules(SecurityGroup securityGroup, ImmutableSet<Rule> ingressRules) {
        assertThat(ingressRules).containsAll(Iterables.transform(securityGroup.getIngressRules(),
            ConvertIngressRuleToRule.FUNCTION));
    }

}

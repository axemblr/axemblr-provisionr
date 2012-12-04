package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupEgressRequest;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.axemblr.provisionr.amazon.AmazonProvisionr;
import com.axemblr.provisionr.amazon.functions.ConvertIpPermissionToRule;
import com.axemblr.provisionr.amazon.functions.ConvertRuleToIpPermission;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.test.ProvisionrLiveTestSupport;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import com.google.common.collect.Lists;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Sets;
import com.sun.org.apache.bcel.internal.generic.NEW;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureSecurityGroupExistsLiveTest extends ProvisionrLiveTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(EnsureSecurityGroupExistsLiveTest.class);

    private final AmazonActivity ACTIVITY = new EnsureSecurityGroupExists();

    private final String BUSINESS_KEY = "j-" + UUID.randomUUID().toString();
    private final String SECURITY_GROUP_NAME = "network-" + BUSINESS_KEY;

    private Provider provider;
    private AmazonEC2 client;

    public EnsureSecurityGroupExistsLiveTest() {
        super(AmazonProvisionr.ID);
    }

    @Before
    public void setUp() {
        provider = collectProviderCredentialsFromSystemProperties()
            .option("region", getProviderProperty("region", "us-east-1")).createProvider();
        LOG.info("Using provider {}", provider);

        // Create the client and make sure we have a clean test environment
        client = ACTIVITY.newAmazonEc2Client(provider);
        deleteSecurityGroupIfExists();
    }

    @After
    public void tearDown() {
        deleteSecurityGroupIfExists();
        client.shutdown();
    }

    private void deleteSecurityGroupIfExists() {
        try {
            client.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupName(SECURITY_GROUP_NAME));

        } catch (AmazonServiceException e) {
            if (!e.getErrorCode().equals("InvalidGroup.NotFound")) {
                throw Throwables.propagate(e);
            }
        }
    }

    @Test
    public void testCreateSecurityGroup() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);

        final ImmutableSet<Rule> ingressRules = ImmutableSet.of(
            Rule.builder().anySource().icmp().createRule(),
            Rule.builder().anySource().port(22).tcp().createRule(),
            Rule.builder().anySource().port(53).udp().createRule()
        );

        final Network network = Network.builder().ingress(ingressRules).createNetwork();
        final Pool pool = Pool.builder().provider(provider).network(network).createPool();

        when(execution.getVariable("pool")).thenReturn(pool);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);

        ACTIVITY.execute(execution);
        assertSecurityGroupExistsWithRules(SECURITY_GROUP_NAME, ingressRules);

        /* If any permissions is removed it should converge on a second run */
        Set<Rule> expectedAfterRevoke = revokeAnyRule(ingressRules);
        assertSecurityGroupExistsWithRules(SECURITY_GROUP_NAME, expectedAfterRevoke);

        ACTIVITY.execute(execution);
        assertSecurityGroupExistsWithRules(SECURITY_GROUP_NAME, ingressRules);

        /* If any permissions is added it should converge on a second run */
        final IpPermission permission = new IpPermission().withIpProtocol("tcp")
            .withIpRanges("0.0.0.0/0").withFromPort(100).withToPort(120);

        client.authorizeSecurityGroupIngress(new AuthorizeSecurityGroupIngressRequest()
            .withGroupName(SECURITY_GROUP_NAME).withIpPermissions(permission));

        assertSecurityGroupExistsWithRules(SECURITY_GROUP_NAME, Sets.<Rule>union(ingressRules,
            ImmutableSet.of(ConvertIpPermissionToRule.FUNCTION.apply(permission))));

        ACTIVITY.execute(execution);
        assertSecurityGroupExistsWithRules(SECURITY_GROUP_NAME, ingressRules);
    }

    private Set<Rule> revokeAnyRule(Set<Rule> ingressRules) {
        List<IpPermission> permissions = newArrayList(transform(ingressRules, ConvertRuleToIpPermission.FUNCTION));
        Collections.shuffle(permissions);

        IpPermission permission = permissions.get(0);
        LOG.info(">> Revoking permission {} on {} for test.", permission, SECURITY_GROUP_NAME);

        client.revokeSecurityGroupIngress(new RevokeSecurityGroupIngressRequest()
            .withGroupName(SECURITY_GROUP_NAME).withIpPermissions(permission));

        return Sets.difference(ingressRules, ImmutableSet.of(ConvertIpPermissionToRule.FUNCTION.apply(permission)));
    }

    public void assertSecurityGroupExistsWithRules(String groupName, final Set<Rule> ingressRules) {
        DescribeSecurityGroupsResult result = client.describeSecurityGroups(
            new DescribeSecurityGroupsRequest().withGroupNames(groupName));

        assertThat(result.getSecurityGroups()).hasSize(1);
        SecurityGroup group = getOnlyElement(result.getSecurityGroups());

        assertThat(group.getIpPermissions()).hasSize(ingressRules.size());
        assertThat(ingressRules).containsAll(transform(group.getIpPermissions(),
            ConvertIpPermissionToRule.FUNCTION));
    }
}

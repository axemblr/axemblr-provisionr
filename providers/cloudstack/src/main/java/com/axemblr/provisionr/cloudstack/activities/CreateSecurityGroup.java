package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Protocol;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.activiti.engine.delegate.DelegateExecution;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.features.SecurityGroupClient;
import static org.jclouds.cloudstack.options.ListSecurityGroupsOptions.Builder.named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSecurityGroup extends CloudStackActivity {

    public static final int DEFAULT_ICMP_CODE = 0;
    public static final int DEFAULT_ICMP_TYPE = 8;
    private static final Logger LOG = LoggerFactory.getLogger(CreateSecurityGroup.class);

    @Override
    public void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution) {
        checkNotNull(pool);
        checkNotNull(cloudStackClient);
        checkNotNull(execution);
        Network network = pool.getNetwork();
        LOG.debug("Creating security group for network {}", network.toString());

        String securityGroupName = String.format("network-%s", execution.getProcessBusinessKey());
        SecurityGroupClient securityGroupClient = cloudStackClient.getSecurityGroupClient();

        try {
            securityGroupClient.createSecurityGroup(securityGroupName);
            SecurityGroup securityGroup = getSecurityGroupByName(securityGroupClient, securityGroupName);

            for (Rule rule : network.getIngress()) {
                if (rule.getProtocol() == Protocol.ICMP) {
                    securityGroupClient.authorizeIngressICMPToCIDRs(securityGroup.getId(), DEFAULT_ICMP_CODE,
                        DEFAULT_ICMP_TYPE, ImmutableList.of(rule.getCidr()));
                } else {
                    securityGroupClient.authorizeIngressPortsToCIDRs(securityGroup.getId(),
                        rule.getProtocol().name(),
                        rule.getPorts().lowerEndpoint(),
                        rule.getPorts().upperEndpoint(),
                        Lists.newArrayList(rule.getCidr()));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception creating security group {} for process {}", securityGroupName);
            Throwables.propagate(e);
        }
    }

    public static SecurityGroup getSecurityGroupByName(SecurityGroupClient securityGroupClient, String securityGroup) {
        return Iterables.getOnlyElement(securityGroupClient.listSecurityGroups(named(securityGroup)));
    }


}
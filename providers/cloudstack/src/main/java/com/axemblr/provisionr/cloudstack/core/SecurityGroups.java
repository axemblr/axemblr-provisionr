package com.axemblr.provisionr.cloudstack.core;

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Protocol;
import com.axemblr.provisionr.api.network.Rule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.IngressRule;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.features.SecurityGroupClient;
import static org.jclouds.cloudstack.options.ListSecurityGroupsOptions.Builder.named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityGroups {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityGroup.class);
    public static final int DEFAULT_ICMP_CODE = 0;
    public static final int DEFAULT_ICMP_TYPE = 8;

    public static String formatNameFromBusinessKey(String processBusinessKey) {
        return String.format("network-%s", processBusinessKey);
    }

    /**
     * Get a SecurityGroup by name.
     *
     * @throws NoSuchElementException if securityGroup does not exist.
     */
    public static SecurityGroup getByName(CloudStackClient cloudStackClient, String securityGroup) {
        return Iterables.getOnlyElement(cloudStackClient
            .getSecurityGroupClient()
            .listSecurityGroups(named(securityGroup)));
    }

    public static Set<SecurityGroup> getAll(CloudStackClient cloudStackClient) {
        return cloudStackClient.getSecurityGroupClient().listSecurityGroups();
    }

    public static void deleteByName(CloudStackClient cloudStackClient, String securityGroupName) {
        try {
            SecurityGroup securityGroup = getByName(cloudStackClient, securityGroupName);
            LOG.info("Deleting SecurityGroup {}", securityGroup.getName());
            cloudStackClient.getSecurityGroupClient().deleteSecurityGroup(securityGroup.getId());
        } catch (NoSuchElementException e) {
            LOG.warn("Exception retrieving SecurityGroup (most likely it does not yet exist){}: {}", securityGroupName, e);
        }
    }

    public static SecurityGroup createSecurityGroup(CloudStackClient cloudStackClient, String securityGroupName) {
        SecurityGroupClient securityGroupClient = cloudStackClient.getSecurityGroupClient();
        return securityGroupClient.createSecurityGroup(securityGroupName);
    }

    public static void deleteNetworkRules(CloudStackClient cloudStackClient, SecurityGroup securityGroup) {
        for (IngressRule rule : securityGroup.getIngressRules()) {
            cloudStackClient.getSecurityGroupClient().revokeIngressRule(rule.getId());
        }
    }

    public static void applyNetworkRules(CloudStackClient cloudStackClient, SecurityGroup securityGroup, Network network) {
        SecurityGroupClient securityGroupClient = cloudStackClient.getSecurityGroupClient();
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
    }
}

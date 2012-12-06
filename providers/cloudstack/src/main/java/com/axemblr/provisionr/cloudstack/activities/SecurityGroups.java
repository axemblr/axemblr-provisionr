package com.axemblr.provisionr.cloudstack.activities;

import com.google.common.collect.Iterables;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.SecurityGroup;
import static org.jclouds.cloudstack.options.ListSecurityGroupsOptions.Builder.named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityGroups {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityGroup.class);

    static String formatSecurityGroupNameFromProcessBusinessKey(String processBusinessKey) {
        return String.format("network-%s", processBusinessKey);
    }

    static SecurityGroup getByName(CloudStackClient cloudStackClient, String securityGroup) {
        return Iterables.getOnlyElement(cloudStackClient
            .getSecurityGroupClient()
            .listSecurityGroups(named(securityGroup)));
    }

    static Set<SecurityGroup> getAll(CloudStackClient cloudStackClient) {
        return cloudStackClient.getSecurityGroupClient().listSecurityGroups();
    }

    static void deleteByName(CloudStackClient cloudStackClient, String securityGroupName) {
        try {
            SecurityGroup securityGroup = getByName(cloudStackClient, securityGroupName);
            LOG.info("Deleting SecurityGroup {}", securityGroup.getName());
            cloudStackClient.getSecurityGroupClient().deleteSecurityGroup(securityGroup.getId());
        } catch (NoSuchElementException e) {
            LOG.warn("Exception retrieving SecurityGroup (most likely it does not yet exist){}: {}", securityGroupName, e);
        }
    }
}

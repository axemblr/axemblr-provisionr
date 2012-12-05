package com.axemblr.provisionr.cloudstack.activities;

import com.google.common.base.Throwables;
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
        try {
            return Iterables.getOnlyElement(cloudStackClient
                .getSecurityGroupClient()
                .listSecurityGroups(named(securityGroup)));
        } catch (NoSuchElementException e1) {
            throw Throwables.propagate(e1);
        } catch (IllegalArgumentException e2) {
            throw new NoSuchElementException(e2.getMessage());
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
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
            LOG.debug("Exception retrieving SecurityGroup (most likely it does not yet exist){}: {}", securityGroupName, e);
        }
    }
}

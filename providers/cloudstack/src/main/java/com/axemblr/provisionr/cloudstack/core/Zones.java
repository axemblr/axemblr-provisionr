package com.axemblr.provisionr.cloudstack.core;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.Set;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Zone;

public class Zones {

    public static boolean hasSecurityGroupEnabled(final CloudStackClient cloudStackClient, final String zoneName) {
        Set<Zone> ourZone = Sets.filter(cloudStackClient.getZoneClient().listZones(), new Predicate<Zone>() {
            @Override
            public boolean apply(Zone input) {
                return input.getName().equals(zoneName);
            }
        });
        return Iterables.getOnlyElement(ourZone).isSecurityGroupsEnabled();
    }
}

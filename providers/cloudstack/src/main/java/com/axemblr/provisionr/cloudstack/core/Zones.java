/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

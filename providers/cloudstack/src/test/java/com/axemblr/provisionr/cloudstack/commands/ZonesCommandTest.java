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

package com.axemblr.provisionr.cloudstack.commands;

import com.google.common.collect.Sets;
import java.util.Set;
import static org.fest.assertions.api.Assertions.assertThat;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.features.ZoneClient;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZonesCommandTest extends CommandTestSupport {

    private final Set<Zone> zones = Sets.newHashSet(Zone.builder()
        .id("zone-1")
        .name("zone-one")
        .securityGroupsEnabled(false)
        .build());

    @Test
    public void testZonesCommandPrintsZones() throws Exception {
        final ZonesCommand zonesCommand = new ZonesCommand(defaultProviderConfig);
        final ZoneClient zoneClient = mock(ZoneClient.class);
        when(client.getZoneClient()).thenReturn(zoneClient);
        when(zoneClient.listZones()).thenReturn(zones);

        zonesCommand.doExecuteWithContext(client, out);
        out.close();

        final String result = byteArrayOutputStream.toString();

        assertThat(result)
            .contains("zone-1")
            .contains("zone-one")
            .contains("false");
    }
}

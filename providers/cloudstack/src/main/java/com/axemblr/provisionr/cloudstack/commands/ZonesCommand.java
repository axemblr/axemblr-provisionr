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

import com.axemblr.provisionr.cloudstack.DefaultProviderConfig;
import java.io.PrintStream;
import java.security.Provider;
import java.security.Security;
import org.apache.felix.gogo.commands.Command;
import org.jclouds.cloudstack.domain.Zone;

@Command(scope = "cloudstack", name = "zones", description = "Manage CloudStack Zones")
public class ZonesCommand extends CommandSupport {

    private static final PrintStream out = System.out;

    public ZonesCommand(DefaultProviderConfig defaultProviderConfig) {
        super(defaultProviderConfig.createProvider().get());
    }

    @Override
    public Object doExecuteWithContext() throws Exception {
        for (Provider provider: Security.getProviders()){
            out.println(provider.toString());
            for (Provider.Service service: provider.getServices()){
                out.println("\t" + service.toString());
            }
        }

        out.printf("CloudStack zones for provider %s\n", getProvider().getId());
        for (Zone zone : getClient().getZoneClient().listZones()) {
            out.printf("%s\n", zone.toString());
        }
        out.println();
        return null;
    }
}

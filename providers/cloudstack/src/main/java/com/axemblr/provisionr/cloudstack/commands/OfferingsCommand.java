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
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.DiskOffering;
import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.ServiceOffering;

@Command(scope = CommandSupport.CLOUDSTACK_SCOPE, name = OfferingsCommand.NAME,
    description = "Commands to list CloudStack Service Offerings")
public class OfferingsCommand extends CommandSupport {

    public static final String NAME = "offerings";

    @Option(name = "s", aliases = "service", description = "List service offerings")
    private boolean serviceOffering;

    @Option(name = "n", aliases = "network", description = "List network offerings")
    private boolean networkOffering;

    @Option(name = "d", aliases = "disk", description = "List disk offerings")
    private boolean diskOffering;

    public OfferingsCommand(DefaultProviderConfig providerConfig) {
        super(providerConfig);
    }

    @Override
    public Object doExecuteWithContext(CloudStackClient client, PrintStream out) throws Exception {
        if (isDiskOfferingListed() || isServiceOfferingListed() || isNetworkOfferingListed()) {
            out.printf("CloudStack Service Offerings for provider %s\n", getProvider().getId());
            listServiceOfferingsIfSpecified(client, out);
            listNetworkOfferingsIfSpecified(client, out);
            listDiskOfferingsIfSpecified(client, out);
        } else {
            out.printf("No option specified. See --help for details.");
        }
        return null;
    }

    private void listDiskOfferingsIfSpecified(CloudStackClient client, PrintStream out) {
        if (isDiskOfferingListed()) {
            for (DiskOffering offering : client.getOfferingClient().listDiskOfferings()) {
                out.printf("---\n%s\n", offering.toString());
            }
        }
    }

    private void listNetworkOfferingsIfSpecified(CloudStackClient client, PrintStream out) {
        if (isNetworkOfferingListed()) {
            for (NetworkOffering offering : client.getOfferingClient().listNetworkOfferings()) {
                out.printf("---\n%s\n", offering.toString());
            }
        }
    }

    private void listServiceOfferingsIfSpecified(CloudStackClient client, PrintStream out) {
        if (isServiceOfferingListed()) {
            for (ServiceOffering offering : client.getOfferingClient().listServiceOfferings()) {
                out.printf("---\n%s\n", offering.toString());
            }
        }
    }

    public boolean isDiskOfferingListed() {
        return diskOffering;
    }

    public void setDiskOffering(boolean diskOffering) {
        this.diskOffering = diskOffering;
    }

    public boolean isNetworkOfferingListed() {
        return networkOffering;
    }

    public void setNetworkOffering(boolean networkOffering) {
        this.networkOffering = networkOffering;
    }

    public boolean isServiceOfferingListed() {
        return serviceOffering;
    }

    public void setServiceOffering(boolean serviceOffering) {
        this.serviceOffering = serviceOffering;
    }
}

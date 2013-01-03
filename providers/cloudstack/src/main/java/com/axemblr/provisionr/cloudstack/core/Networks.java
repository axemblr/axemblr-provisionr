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
import java.util.NoSuchElementException;
import java.util.Set;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Network;

public class Networks {

    private Networks() {
    }

    public static String formatNameFromBusinessKey(String processBusinessKey) {
        return String.format("networks-%s", processBusinessKey);
    }

    /**
     * Returns the first network with the given name.
     *
     * @throws NoSuchElementException   if no network is found
     * @throws IllegalArgumentException if more networks with the same name are found
     */
    public static Network getByName(CloudStackClient client, final String networkName) {
        Set<Network> networks = Sets.filter(client.getNetworkClient().listNetworks(), new Predicate<Network>() {
            @Override
            public boolean apply(Network input) {
                return input.getName().equals(networkName);
            }
        });
        return Iterables.getOnlyElement(networks);
    }
}

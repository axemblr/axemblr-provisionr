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

package com.axemblr.provisionr.api.network;

import com.axemblr.provisionr.api.util.WithOptions;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Network extends WithOptions {

    public static NetworkBuilder builder() {
        return new NetworkBuilder();
    }

    private final String type;
    private final Set<Rule> ingress;

    Network(String type, Set<Rule> ingress, Map<String, String> options) {
        super(options);
        this.type = checkNotNull(type, "type is null");
        this.ingress = ImmutableSet.copyOf(ingress);
    }

    /**
     * Implementation specific network type information
     */
    public String getType() {
        return type;
    }

    /**
     * Set of rules for incoming packages
     */
    public Set<Rule> getIngress() {
        return ingress;
    }

    public NetworkBuilder toBuilder() {
        return builder().type(type).ingress(ingress).options(getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, ingress, getOptions());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Network other = (Network) obj;
        return Objects.equal(this.type, other.type)
            && Objects.equal(this.ingress, other.ingress)
            && Objects.equal(this.getOptions(), other.getOptions());
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
            .add("type", type).add("ingress", ingress)
            .add("options", getOptions()).toString();
    }
}

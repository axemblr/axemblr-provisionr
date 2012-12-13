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

import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import java.io.Serializable;

public class Rule implements Serializable {

    public static final String CIDR_ALL = "0.0.0.0/0";
    public static final Range<Integer> EMPTY_RANGE = Ranges.openClosed(-1, -1);

    public static RuleBuilder builder() {
        return new RuleBuilder();
    }

    private final String cidr;
    private final Range<Integer> ports;
    private final Protocol protocol;

    Rule(String cidr, Range<Integer> ports, Protocol protocol) {
        this.ports = checkNotNull(ports, "ports is null");
        this.cidr = checkNotNull(cidr, "cidr is null");
        this.protocol = checkNotNull(protocol, "protocol is null");
    }

    public String getCidr() {
        return cidr;
    }

    public Range<Integer> getPorts() {
        return ports;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public RuleBuilder toBuilder() {
        return builder().cidr(cidr).ports(ports).protocol(protocol);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cidr, ports, protocol);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rule other = (Rule) obj;
        return Objects.equal(this.cidr, other.cidr)
            && Objects.equal(this.ports, other.ports)
            && Objects.equal(this.protocol, other.protocol);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
            .add("cidr", cidr).add("ports", ports).add("protocol", protocol).toString();
    }
}

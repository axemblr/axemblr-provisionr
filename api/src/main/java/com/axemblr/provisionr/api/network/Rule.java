package com.axemblr.provisionr.api.network;

import com.google.common.base.Objects;
import com.google.common.collect.Range;
import java.io.Serializable;

public class Rule implements Serializable {

    public static final String CIDR_ALL = "0.0.0.0/0";

    public static RuleBuilder builder() {
        return new RuleBuilder();
    }

    private final String cidr;
    private final Range<Integer> ports;
    private final Protocol protocol;

    Rule(String cidr, Range<Integer> ports, Protocol protocol) {
        this.cidr = cidr;
        this.ports = ports;
        this.protocol = protocol;
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

package com.axemblr.provisionr.api.network;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

public class RuleBuilder {

    private String cidr = Rule.CIDR_ALL;
    private Range<Integer> ports = Rule.EMPTY_RANGE;
    private Protocol protocol;

    public RuleBuilder cidr(String cidr) {
        this.cidr = checkNotNull(cidr, "cidr is null");
        return this;
    }

    public RuleBuilder anySource() {
        this.cidr = Rule.CIDR_ALL;
        return this;
    }

    public RuleBuilder ports(Range<Integer> ports) {
        checkArgument(ports.hasUpperBound(), "ports should have a closed upper bound");
        checkArgument(ports.hasLowerBound(), "ports should have a closed lower bound ");

        checkArgument(ports.lowerEndpoint() > 0, "ports should be a positive range");
        checkArgument(ports.upperEndpoint() < 65535, "ports upper bound should less than 65535");

        this.ports = checkNotNull(ports, "ports is null");
        return this;
    }

    public RuleBuilder ports(int lowerPort, int upperPort) {
        return ports(Ranges.closed(lowerPort, upperPort));
    }

    public RuleBuilder port(int port) {
        return ports(Ranges.singleton(port));
    }

    public RuleBuilder protocol(Protocol protocol) {
        this.protocol = checkNotNull(protocol, "protocol is null");
        return this;
    }

    public RuleBuilder tcp() {
        return protocol(Protocol.TCP);
    }

    public RuleBuilder udp() {
        return protocol(Protocol.UDP);
    }

    public RuleBuilder icmp() {
        return protocol(Protocol.ICMP);
    }

    public Rule createRule() {
        return new Rule(cidr, ports, protocol);
    }
}
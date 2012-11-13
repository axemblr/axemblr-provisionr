package com.axemblr.provisionr.api.network;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

public class RuleBuilder {

    private String cidr;
    private Range<Integer> ports;
    private Protocol protocol;

    public RuleBuilder cidr(String cidr) {
        this.cidr = cidr;
        return this;
    }

    public RuleBuilder anySource() {
        this.cidr = Rule.CIDR_ALL;
        return this;
    }

    public RuleBuilder ports(Range<Integer> ports) {
        this.ports = ports;
        return this;
    }

    public RuleBuilder port(int port) {
        this.ports = Ranges.singleton(port);
        return this;
    }

    public RuleBuilder protocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public Rule createRule() {
        return new Rule(cidr, ports, protocol);
    }
}
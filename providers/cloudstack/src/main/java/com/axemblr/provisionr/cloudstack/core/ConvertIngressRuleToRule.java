package com.axemblr.provisionr.cloudstack.core;

import com.axemblr.provisionr.api.network.Protocol;
import com.axemblr.provisionr.api.network.Rule;
import com.google.common.base.Function;
import com.google.common.collect.Ranges;
import org.jclouds.cloudstack.domain.IngressRule;

public enum ConvertIngressRuleToRule implements Function<IngressRule, Rule> {
    FUNCTION;

    @Override
    public Rule apply(IngressRule input) {
        Rule rule;
        if (input.getProtocol().equalsIgnoreCase("icmp")) {
            rule = Rule.builder()
                .anySource()
                .protocol(Protocol.valueOf(input.getProtocol().toUpperCase()))
                .cidr(input.getCIDR())
                .createRule();
        } else {
            rule = Rule.builder()
                .anySource()
                .protocol(Protocol.valueOf(input.getProtocol().toUpperCase()))
                .ports(input.getStartPort(), input.getEndPort())
                .cidr(input.getCIDR())
                .createRule();
        }
        return rule;
    }
}

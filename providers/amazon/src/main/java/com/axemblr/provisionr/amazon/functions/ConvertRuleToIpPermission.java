package com.axemblr.provisionr.amazon.functions;

import com.amazonaws.services.ec2.model.IpPermission;
import com.axemblr.provisionr.api.network.Rule;
import com.google.common.base.Function;

public enum ConvertRuleToIpPermission implements Function<Rule, IpPermission> {
    FUNCTION;

    @Override
    public IpPermission apply(Rule rule) {
        IpPermission permission = new IpPermission()
            .withIpProtocol(rule.getProtocol().toString().toLowerCase())
            .withIpRanges(rule.getCidr());

        if (rule.getPorts() != null) {
            permission.withFromPort(rule.getPorts().lowerEndpoint())
                .withToPort(rule.getPorts().upperEndpoint());
        } else {
            permission.withFromPort(-1).withToPort(-1);
        }
        return permission;
    }
}

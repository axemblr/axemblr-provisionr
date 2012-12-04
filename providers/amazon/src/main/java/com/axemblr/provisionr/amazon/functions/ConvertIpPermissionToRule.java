package com.axemblr.provisionr.amazon.functions;

import com.amazonaws.services.ec2.model.IpPermission;
import com.axemblr.provisionr.api.network.Protocol;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.network.RuleBuilder;
import com.google.common.base.Function;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.Ranges;

public enum ConvertIpPermissionToRule implements Function<IpPermission, Rule> {
    FUNCTION;

    @Override
    public Rule apply(IpPermission ipPermission) {
        final RuleBuilder builder = Rule.builder().cidr(getOnlyElement(ipPermission.getIpRanges()))
            .protocol(Protocol.valueOf(ipPermission.getIpProtocol().toUpperCase()));

        if (!ipPermission.getIpProtocol().equals("icmp")) {
            builder.ports(Ranges.<Integer>closed(ipPermission.getFromPort(), ipPermission.getToPort()));
        }

        return builder.createRule();
    }
}

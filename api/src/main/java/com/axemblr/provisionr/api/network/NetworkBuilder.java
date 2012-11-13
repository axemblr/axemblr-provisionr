package com.axemblr.provisionr.api.network;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Set;

public class NetworkBuilder {

    private String type = "default";
    private ImmutableSet.Builder<Rule> incoming = ImmutableSet.<Rule>builder();

    public NetworkBuilder type(String type) {
        this.type = type;
        return this;
    }

    public NetworkBuilder incoming(Set<Rule> incoming) {
        this.incoming = ImmutableSet.<Rule>builder().addAll(incoming);
        return this;
    }

    public NetworkBuilder addRules(Rule... rules) {
        return addRules(Lists.newArrayList(rules));
    }

    public NetworkBuilder addRules(Iterable<Rule> rules) {
        this.incoming.addAll(rules);
        return this;
    }

    public Network createNetwork() {
        return new Network(type, incoming.build());
    }
}
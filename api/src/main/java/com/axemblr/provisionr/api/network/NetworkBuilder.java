package com.axemblr.provisionr.api.network;

import com.axemblr.provisionr.api.util.BuilderWithOptions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Set;

public class NetworkBuilder extends BuilderWithOptions<NetworkBuilder> {

    private String type = "default";
    private ImmutableSet.Builder<Rule> ingress = ImmutableSet.<Rule>builder();

    @Override
    protected NetworkBuilder getThis() {
        return this;
    }

    public NetworkBuilder type(String type) {
        this.type = checkNotNull(type, "type is null");
        return this;
    }

    public NetworkBuilder ingress(Set<Rule> incoming) {
        this.ingress = ImmutableSet.<Rule>builder().addAll(incoming);
        return this;
    }

    public NetworkBuilder addRules(Rule... rules) {
        return addRules(Lists.newArrayList(rules));
    }

    public NetworkBuilder addRules(Iterable<Rule> rules) {
        this.ingress.addAll(rules);
        return this;
    }

    public Network createNetwork() {
        return new Network(type, ingress.build(), buildOptions());
    }
}
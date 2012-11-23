package com.axemblr.provisionr.api.network;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class Network implements Serializable {

    public static NetworkBuilder builder() {
        return new NetworkBuilder();
    }

    private final String type;
    private final Set<Rule> incoming;
    private final Map<String, String> options;

    public Network(String type, Set<Rule> incoming, Map<String, String> options) {
        this.type = Optional.fromNullable(type).or("default");
        this.incoming = ImmutableSet.copyOf(incoming);
        this.options = ImmutableMap.copyOf(options);
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
    public Set<Rule> getIncoming() {
        return incoming;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public NetworkBuilder toBuilder() {
        return builder().type(type).incoming(incoming).options(options);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, incoming, options);
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
            && Objects.equal(this.incoming, other.incoming)
            && Objects.equal(this.options, other.options);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
            .add("type", type).add("incoming", incoming)
            .add("options", options).toString();
    }
}

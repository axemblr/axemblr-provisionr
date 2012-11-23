package com.axemblr.provisionr.api.network;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class NetworkTest {

    @Test
    public void testSerialization() {
        final Rule sshRule = Rule.builder().anySource().port(22).createRule();

        Network network = Network.builder().type("default")
            .addRules(
                sshRule,
                Rule.builder().anySource().port(80).createRule(),
                Rule.builder().anySource().protocol(Protocol.ICMP).createRule())
            .createNetwork();

        assertThat(network.getType()).isEqualTo("default");
        assertThat(network.getIncoming()).contains(sshRule);

        assertSerializable(network, Network.class);
    }
}

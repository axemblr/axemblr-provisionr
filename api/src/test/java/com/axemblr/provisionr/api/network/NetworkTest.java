package com.axemblr.provisionr.api.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class NetworkTest {

    @Test
    public void testCreateNetwork() {
        final Rule sshRule = Rule.builder().anySource().port(22).createRule();

        Network network = Network.builder().type("default").addRules(
            sshRule,
            Rule.builder().anySource().port(80).createRule()
        ).createNetwork();

        assertEquals(network.getType(), "default");
        assertTrue(network.getIncoming().contains(sshRule));
    }
}

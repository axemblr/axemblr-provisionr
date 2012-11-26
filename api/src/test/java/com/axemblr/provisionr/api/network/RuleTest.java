package com.axemblr.provisionr.api.network;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import org.junit.Test;

public class RuleTest {

    @Test
    public void testSerialization() {
        Rule rule = Rule.builder().anySource().port(80).protocol(Protocol.UDP).createRule();
        assertSerializable(rule, Rule.class);
    }
}

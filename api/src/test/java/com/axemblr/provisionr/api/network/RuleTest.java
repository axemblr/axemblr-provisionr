package com.axemblr.provisionr.api.network;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class RuleTest {

    @Test
    public void testSerialization() {
        Rule rule = Rule.builder().anySource().port(80).protocol(Protocol.UDP).createRule();
        assertThat(rule.toBuilder().createRule()).isEqualTo(rule);
        assertSerializable(rule, Rule.class);
    }
}

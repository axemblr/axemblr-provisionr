package com.axemblr.provisionr.cloudstack.functions;

import com.axemblr.provisionr.api.network.Protocol;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.cloudstack.activities.CreateSecurityGroup;
import static org.fest.assertions.api.Assertions.assertThat;
import org.jclouds.cloudstack.domain.IngressRule;
import org.junit.Test;

public class ConversionsTest {


    @Test
    public void testConvertIngressRuleToRuleICMP() throws Exception {
        final IngressRule ingressRule = IngressRule.builder()
            .id("rule1")
            .protocol("icmp")
            .ICMPCode(CreateSecurityGroup.DEFAULT_ICMP_CODE)
            .ICMPType(CreateSecurityGroup.DEFAULT_ICMP_TYPE)
            .CIDR("10.0.0.0/24")
            .build();

        Rule rule = ConvertIngressRuleToRule.FUNCTION.apply(ingressRule);
        assertThat(rule.getProtocol()).isEqualTo(Protocol.ICMP);
        assertThat(rule.getCidr()).isEqualTo(ingressRule.getCIDR());
    }

    @Test
    public void testConvertSinglePortRangeIngressRuleToRule() throws Exception {
        final IngressRule ingressRule = IngressRule.builder()
            .id("rule1")
            .protocol("tcp")
            .startPort(22)
            .endPort(22)
            .CIDR("0.0.0.1/24")
            .build();

        Rule rule = ConvertIngressRuleToRule.FUNCTION.apply(ingressRule);
        assertThat(rule.getProtocol()).isEqualTo(Protocol.TCP);
        assertThat(rule.getCidr()).isEqualTo(ingressRule.getCIDR());
        assertThat(rule.getPorts().lowerEndpoint()).isEqualTo(ingressRule.getStartPort());
        assertThat(rule.getPorts().upperEndpoint()).isEqualTo(ingressRule.getEndPort());
    }
}

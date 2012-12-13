/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axemblr.provisionr.cloudstack.core;

import com.axemblr.provisionr.api.network.Protocol;
import com.axemblr.provisionr.api.network.Rule;
import static org.fest.assertions.api.Assertions.assertThat;
import org.jclouds.cloudstack.domain.IngressRule;
import org.junit.Test;

public class ConversionsTest {

    @Test
    public void testConvertIngressRuleToRuleICMP() throws Exception {
        final IngressRule ingressRule = IngressRule.builder()
            .id("rule1")
            .protocol("icmp")
            .ICMPCode(SecurityGroups.DEFAULT_ICMP_CODE)
            .ICMPType(SecurityGroups.DEFAULT_ICMP_TYPE)
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

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

package com.axemblr.provisionr.amazon.functions;

import com.amazonaws.services.ec2.model.IpPermission;
import com.axemblr.provisionr.api.network.Protocol;
import com.axemblr.provisionr.api.network.Rule;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class ConvertIpPermissionToRuleAndBackTest {

    @Test
    public void testConvertTcpIpPermissionToRuleAndBack() {
        IpPermission expected = new IpPermission().withFromPort(1).withToPort(1024)
            .withIpProtocol("tcp").withIpRanges("0.0.0.0/0");

        Rule rule = ConvertIpPermissionToRule.FUNCTION.apply(expected);
        assertNotNull(rule);

        assertThat(rule.getCidr()).isEqualTo("0.0.0.0/0");
        assertThat(rule.getProtocol()).isEqualTo(Protocol.TCP);

        assertThat(rule.getPorts().lowerEndpoint()).isEqualTo(1);
        assertThat(rule.getPorts().upperEndpoint()).isEqualTo(1024);

        IpPermission actual = ConvertRuleToIpPermission.FUNCTION.apply(rule);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testConvertIcmpPermissionToRule() {
        IpPermission expected = new IpPermission().withIpProtocol("icmp")
            .withFromPort(-1).withToPort(-1).withIpRanges("0.0.0.0/0");

        Rule rule = ConvertIpPermissionToRule.FUNCTION.apply(expected);
        assertNotNull(rule);

        assertThat(rule.getProtocol()).isEqualTo(Protocol.ICMP);
        assertThat(rule.getCidr()).isEqualTo("0.0.0.0/0");

        IpPermission actual = ConvertRuleToIpPermission.FUNCTION.apply(rule);
        assertThat(actual).isEqualTo(expected);
    }
}

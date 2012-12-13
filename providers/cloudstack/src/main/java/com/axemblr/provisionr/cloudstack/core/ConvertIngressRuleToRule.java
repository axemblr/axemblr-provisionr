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
import com.google.common.base.Function;
import org.jclouds.cloudstack.domain.IngressRule;

public enum ConvertIngressRuleToRule implements Function<IngressRule, Rule> {
    FUNCTION;

    @Override
    public Rule apply(IngressRule input) {
        Rule rule;
        if (input.getProtocol().equalsIgnoreCase("icmp")) {
            rule = Rule.builder()
                .anySource()
                .protocol(Protocol.valueOf(input.getProtocol().toUpperCase()))
                .cidr(input.getCIDR())
                .createRule();
        } else {
            rule = Rule.builder()
                .anySource()
                .protocol(Protocol.valueOf(input.getProtocol().toUpperCase()))
                .ports(input.getStartPort(), input.getEndPort())
                .cidr(input.getCIDR())
                .createRule();
        }
        return rule;
    }
}

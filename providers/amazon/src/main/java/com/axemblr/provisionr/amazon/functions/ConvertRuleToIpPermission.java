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
import com.axemblr.provisionr.api.network.Rule;
import com.google.common.base.Function;

public enum ConvertRuleToIpPermission implements Function<Rule, IpPermission> {
    FUNCTION;

    @Override
    public IpPermission apply(Rule rule) {
        IpPermission permission = new IpPermission()
            .withIpProtocol(rule.getProtocol().toString().toLowerCase())
            .withIpRanges(rule.getCidr());

        if (!rule.getPorts().isEmpty()) {
            permission.withFromPort(rule.getPorts().lowerEndpoint())
                .withToPort(rule.getPorts().upperEndpoint());
        } else {
            permission.withFromPort(-1).withToPort(-1);
        }
        return permission;
    }
}

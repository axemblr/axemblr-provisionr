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

package com.axemblr.provisionr.api.pool;

import com.axemblr.provisionr.api.util.BuilderWithOptions;
import static com.google.common.base.Preconditions.checkArgument;

public class MachineBuilder extends BuilderWithOptions<MachineBuilder> {

    private String externalId;
    private String publicDnsName;
    private String publicIp;
    private String privateDnsName;
    private String privateIp;
    private int sshPort = 22;

    @Override
    protected MachineBuilder getThis() {
        return this;
    }

    public MachineBuilder externalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public MachineBuilder publicDnsName(String publicDnsName) {
        this.publicDnsName = publicDnsName;
        return this;
    }

    public MachineBuilder publicIp(String publicIp) {
        this.publicIp = publicIp;
        return this;
    }

    public MachineBuilder privateDnsName(String privateDnsName) {
        this.privateDnsName = privateDnsName;
        return this;
    }

    public MachineBuilder privateIp(String privateIp) {
        this.privateIp = privateIp;
        return this;
    }

    public MachineBuilder sshPort(int sshPort) {
        checkArgument(sshPort > 0 && sshPort < 65535, "invalid port number for ssh");
        this.sshPort = sshPort;
        return this;
    }

    public Machine createMachine() {
        return new Machine(externalId, publicDnsName, publicIp,
            privateDnsName, privateIp, sshPort, buildOptions());
    }
}
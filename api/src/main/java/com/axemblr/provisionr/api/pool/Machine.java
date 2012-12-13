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

import com.axemblr.provisionr.api.util.WithOptions;
import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;

/**
 * Details about a running machine from a pool
 * <p/>
 * Used to connect to that machine for configuration.
 */
public class Machine extends WithOptions {

    public static MachineBuilder builder() {
        return new MachineBuilder();
    }

    /**
     * External provider specific unique ID
     */
    private final String externalId;

    /**
     * Public DNS reachable over the internet
     */
    private final String publicDnsName;

    /**
     * Public IP address reachable over the internet
     */
    private final String publicIp;


    /**
     * Private DNS name (inside the cloud provider network)
     */
    private final String privateDnsName;

    /**
     * Private IP address (inside the cloud provider network)
     */
    private final String privateIp;

    Machine(String externalId, String publicDnsName, String publicIp,
            String privateDnsName, String privateIp, Map<String, String> options) {
        super(options);
        this.externalId = checkNotNull(externalId, "externalId is null");
        this.publicDnsName = checkNotNull(publicDnsName, "publicDnsName is null");
        this.publicIp = checkNotNull(publicIp, "publicIp is null");
        this.privateDnsName = checkNotNull(privateDnsName, "privateDnsName is null");
        this.privateIp = checkNotNull(privateIp, "privateIp is null");
    }

    /**
     * External provider specific unique ID
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Public DNS reachable over the internet
     */
    public String getPublicDnsName() {
        return publicDnsName;
    }

    /**
     * Public IP address reachable over the internet
     */
    public String getPublicIp() {
        return publicIp;
    }

    /**
     * Private DNS name (inside the cloud provider network)
     */
    public String getPrivateDnsName() {
        return privateDnsName;
    }

    /**
     * Private IP address (inside the cloud provider network)
     */
    public String getPrivateIp() {
        return privateIp;
    }

    public MachineBuilder toBuilder() {
        return builder().externalId(externalId).publicDnsName(publicDnsName).publicIp(publicIp)
            .privateDnsName(privateDnsName).privateIp(privateIp).options(getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(externalId, publicDnsName, publicIp,
            privateDnsName, privateIp, getOptions());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Machine other = (Machine) obj;
        return Objects.equal(this.externalId, other.externalId)
            && Objects.equal(this.publicDnsName, other.publicDnsName)
            && Objects.equal(this.publicIp, other.publicIp)
            && Objects.equal(this.privateDnsName, other.privateDnsName)
            && Objects.equal(this.privateIp, other.privateIp)
            && Objects.equal(this.getOptions(), other.getOptions());
    }

    @Override
    public String toString() {
        return "Machine{" +
            "externalId='" + externalId + '\'' +
            ", publicDnsName='" + publicDnsName + '\'' +
            ", publicIp='" + publicIp + '\'' +
            ", privateDnsName='" + privateDnsName + '\'' +
            ", privateIp='" + privateIp + '\'' +
            ", options=" + getOptions() +
            '}';
    }
}

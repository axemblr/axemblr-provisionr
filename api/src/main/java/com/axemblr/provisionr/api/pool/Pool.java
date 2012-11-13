package com.axemblr.provisionr.api.pool;

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Objects;
import java.io.Serializable;

public class Pool implements Serializable {

    private final Provider provider;
    private final Network network;

    private final String osType;
    private final String hardwareType;

    private final int minSize;
    private final int maxSize;

    private final int bootstrapTimeInSeconds;

    public Pool(Provider provider, Network network, String osType, String hardwareType,
                int minSize, int maxSize, int bootstrapTimeInSeconds) {
        this.provider = provider;
        this.network = network;
        this.osType = osType;
        this.hardwareType = hardwareType;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.bootstrapTimeInSeconds = bootstrapTimeInSeconds;
    }

    public Provider getProvider() {
        return provider;
    }

    public Network getNetwork() {
        return network;
    }

    public String getOsType() {
        return osType;
    }

    public String getHardwareType() {
        return hardwareType;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getBootstrapTimeInSeconds() {
        return bootstrapTimeInSeconds;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(provider, network, osType, hardwareType,
            minSize, maxSize, bootstrapTimeInSeconds);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pool other = (Pool) obj;
        return Objects.equal(this.provider, other.provider)
            && Objects.equal(this.network, other.network)
            && Objects.equal(this.osType, other.osType)
            && Objects.equal(this.hardwareType, other.hardwareType)
            && Objects.equal(this.minSize, other.minSize)
            && Objects.equal(this.maxSize, other.maxSize)
            && Objects.equal(this.bootstrapTimeInSeconds, other.bootstrapTimeInSeconds);
    }

    @Override
    public String toString() {
        return "Pool{" +
            "provider=" + provider +
            ", network=" + network +
            ", osType='" + osType + '\'' +
            ", hardwareType='" + hardwareType + '\'' +
            ", minSize=" + minSize +
            ", maxSize=" + maxSize +
            ", bootstrapTimeInSeconds=" + bootstrapTimeInSeconds +
            '}';
    }
}

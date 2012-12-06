package com.axemblr.provisionr.api.pool;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;

public class Pool implements Serializable {

    public static PoolBuilder builder() {
        return new PoolBuilder();
    }

    private final Provider provider;
    private final Network network;

    private final AdminAccess adminAccess;
    private final Software software;
    private final Hardware hardware;

    private final int minSize;
    private final int expectedSize;

    private final boolean cacheBaseImage;
    private final int bootstrapTimeInSeconds;

    private final Map<String, String> options;

    Pool(Provider provider, Network network, AdminAccess adminAccess, Software software, Hardware hardware,
         int minSize, int expectedSize, boolean cacheBaseImage, int bootstrapTimeInSeconds,
         Map<String, String> options
    ) {
        this.provider = provider;
        this.network = network;
        this.adminAccess = adminAccess;
        this.software = software;
        this.hardware = hardware;
        this.minSize = minSize;
        this.expectedSize = expectedSize;
        this.cacheBaseImage = cacheBaseImage;
        this.bootstrapTimeInSeconds = bootstrapTimeInSeconds;
        this.options = ImmutableMap.copyOf(options);
    }

    public Provider getProvider() {
        return provider;
    }

    public Network getNetwork() {
        return network;
    }

    public AdminAccess getAdminAccess() {
        return adminAccess;
    }

    public Software getSoftware() {
        return software;
    }

    public Hardware getHardware() {
        return hardware;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getExpectedSize() {
        return expectedSize;
    }

    public boolean isCacheBaseImage() {
        return cacheBaseImage;
    }

    /**
     * The maximum amount of time to go from 0 to minSize
     */
    public int getBootstrapTimeInSeconds() {
        return bootstrapTimeInSeconds;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public PoolBuilder toBuilder() {
        return builder().provider(provider).network(network).adminAccess(adminAccess).software(software)
            .hardware(hardware).minSize(minSize).cacheBaseImage(cacheBaseImage).expectedSize(expectedSize)
            .bootstrapTimeInSeconds(bootstrapTimeInSeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(provider, network, adminAccess, software, hardware,
            minSize, expectedSize, cacheBaseImage, bootstrapTimeInSeconds, options);
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
        return Objects.equal(this.provider, other.provider) && Objects.equal(this.network, other.network)
            && Objects.equal(this.adminAccess, other.adminAccess) && Objects.equal(this.software, other.software)
            && Objects.equal(this.hardware, other.hardware) && Objects.equal(this.minSize, other.minSize)
            && Objects.equal(this.expectedSize, other.expectedSize)
            && this.cacheBaseImage == other.cacheBaseImage
            && Objects.equal(this.bootstrapTimeInSeconds, other.bootstrapTimeInSeconds)
            && Objects.equal(this.options, other.options);
    }

    @Override
    public String toString() {
        return "Pool{" +
            "provider=" + provider +
            ", network=" + network +
            ", adminAccess=" + adminAccess +
            ", software=" + software +
            ", hardware=" + hardware +
            ", minSize=" + minSize +
            ", cacheBaseImage=" + cacheBaseImage +
            ", expectedSize=" + expectedSize +
            ", bootstrapTimeInSeconds=" + bootstrapTimeInSeconds +
            '}';
    }
}

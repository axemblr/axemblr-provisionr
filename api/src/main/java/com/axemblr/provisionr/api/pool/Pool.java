package com.axemblr.provisionr.api.pool;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.util.WithOptions;
import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;

public class Pool extends WithOptions {

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

    Pool(Provider provider, Network network, AdminAccess adminAccess, Software software, Hardware hardware,
         int minSize, int expectedSize, boolean cacheBaseImage, int bootstrapTimeInSeconds,
         Map<String, String> options
    ) {
        super(options);

        checkArgument(minSize > 0, "minSize should be positive");
        checkArgument(minSize <= expectedSize, "minSize should be smaller or equal to expectedSize");
        checkArgument(bootstrapTimeInSeconds > 0, "bootstrapTimeInSeconds should be positive");

        this.provider = checkNotNull(provider, "provider is null");
        this.network = checkNotNull(network, "network is null");
        this.adminAccess = checkNotNull(adminAccess, "adminAccess is null");
        this.software = checkNotNull(software, "software is null");
        this.hardware = checkNotNull(hardware, "hardware is null");
        this.minSize = minSize;
        this.expectedSize = expectedSize;
        this.cacheBaseImage = cacheBaseImage;
        this.bootstrapTimeInSeconds = bootstrapTimeInSeconds;
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

    public PoolBuilder toBuilder() {
        return builder().provider(provider).network(network).adminAccess(adminAccess).software(software)
            .hardware(hardware).minSize(minSize).cacheBaseImage(cacheBaseImage).expectedSize(expectedSize)
            .bootstrapTimeInSeconds(bootstrapTimeInSeconds).options(getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(provider, network, adminAccess, software, hardware,
            minSize, expectedSize, cacheBaseImage, bootstrapTimeInSeconds, getOptions());
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
            && Objects.equal(this.getOptions(), other.getOptions());
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
            ", options=" + getOptions() +
            '}';
    }
}

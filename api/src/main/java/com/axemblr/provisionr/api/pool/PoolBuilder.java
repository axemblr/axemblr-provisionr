package com.axemblr.provisionr.api.pool;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.util.BuilderWithOptions;
import static com.google.common.base.Preconditions.checkNotNull;

public class PoolBuilder extends BuilderWithOptions<PoolBuilder> {

    private Provider provider;
    private Network network;

    private AdminAccess adminAccess;
    private Software software;
    private Hardware hardware;

    private int minSize = -1;
    private int expectedSize = -1;

    private boolean cacheBaseImage = false;
    private int bootstrapTimeInSeconds = 15 * 60;

    @Override
    protected PoolBuilder getThis() {
        return this;
    }

    public PoolBuilder provider(Provider provider) {
        this.provider = provider;
        return this;
    }

    public PoolBuilder network(Network network) {
        this.network = network;
        return this;
    }

    public PoolBuilder adminAccess(AdminAccess adminAccess) {
        this.adminAccess = checkNotNull(adminAccess, "adminAccess is null");
        return this;
    }

    public PoolBuilder software(Software software) {
        this.software = software;
        return this;
    }

    public PoolBuilder hardware(Hardware hardware) {
        this.hardware = hardware;
        return this;
    }

    public PoolBuilder minSize(int minSize) {
        this.minSize = minSize;
        return this;
    }

    public PoolBuilder expectedSize(int expectedSize) {
        this.expectedSize = expectedSize;
        return this;
    }

    public PoolBuilder cacheBaseImage(boolean cacheBaseImage) {
        this.cacheBaseImage = cacheBaseImage;
        return this;
    }

    public PoolBuilder bootstrapTimeInSeconds(int bootstrapTimeInSeconds) {
        this.bootstrapTimeInSeconds = bootstrapTimeInSeconds;
        return this;
    }

    public Pool createPool() {
        return new Pool(provider, network, adminAccess, software, hardware, minSize,
            expectedSize, cacheBaseImage, bootstrapTimeInSeconds, buildOptions());
    }
}
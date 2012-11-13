package com.axemblr.provisionr.api.pool;

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.provider.Provider;

public class PoolBuilder {

    private Provider provider;
    private Network network;

    private String osType;
    private String hardwareType;

    private int minSize;
    private int maxSize;

    private int bootstrapTimeInSeconds;

    public PoolBuilder provider(Provider provider) {
        this.provider = provider;
        return this;
    }

    public PoolBuilder network(Network network) {
        this.network = network;
        return this;
    }

    public PoolBuilder osType(String osType) {
        this.osType = osType;
        return this;
    }

    public PoolBuilder hardwareType(String hardwareType) {
        this.hardwareType = hardwareType;
        return this;
    }

    public PoolBuilder minSize(int minSize) {
        this.minSize = minSize;
        return this;
    }

    public PoolBuilder maxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public PoolBuilder bootstrapTimeInSeconds(int bootstrapTimeInSeconds) {
        this.bootstrapTimeInSeconds = bootstrapTimeInSeconds;
        return this;
    }

    public Pool createPool() {
        return new Pool(provider, network, osType, hardwareType,
            minSize, maxSize, bootstrapTimeInSeconds);
    }
}
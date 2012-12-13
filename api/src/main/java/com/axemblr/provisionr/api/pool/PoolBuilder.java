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

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.util.BuilderWithOptions;
import static com.google.common.base.Preconditions.checkArgument;
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
        this.provider = checkNotNull(provider, "provider is null");
        return this;
    }

    public PoolBuilder network(Network network) {
        this.network = checkNotNull(network, "network is null");
        return this;
    }

    public PoolBuilder adminAccess(AdminAccess adminAccess) {
        this.adminAccess = checkNotNull(adminAccess, "adminAccess is null");
        return this;
    }

    public PoolBuilder software(Software software) {
        this.software = checkNotNull(software, "software is null");
        return this;
    }

    public PoolBuilder hardware(Hardware hardware) {
        this.hardware = checkNotNull(hardware, "hardware is null");
        return this;
    }

    public PoolBuilder minSize(int minSize) {
        checkArgument(minSize > 0, "minSize should be positive");
        this.minSize = minSize;
        return this;
    }

    public PoolBuilder expectedSize(int expectedSize) {
        checkArgument(expectedSize > 0, "expectedSize should be positive");
        this.expectedSize = expectedSize;
        return this;
    }

    public PoolBuilder cacheBaseImage(boolean cacheBaseImage) {
        this.cacheBaseImage = cacheBaseImage;
        return this;
    }

    public PoolBuilder bootstrapTimeInSeconds(int bootstrapTimeInSeconds) {
        checkArgument(bootstrapTimeInSeconds > 0, "bootstrapTimeInSeconds should be positive");
        this.bootstrapTimeInSeconds = bootstrapTimeInSeconds;
        return this;
    }

    public Pool createPool() {
        return new Pool(provider, network, adminAccess, software, hardware, minSize,
            expectedSize, cacheBaseImage, bootstrapTimeInSeconds, buildOptions());
    }
}
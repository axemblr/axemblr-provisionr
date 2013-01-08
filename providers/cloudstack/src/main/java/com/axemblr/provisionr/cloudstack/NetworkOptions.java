/*
 * Copyright (c) 2013 S.C. Axemblr Software Solutions S.R.L
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

package com.axemblr.provisionr.cloudstack;

/**
 * {@link com.axemblr.provisionr.api.network.Network Network} configuration is passed via the
 * {@link com.axemblr.provisionr.api.pool.Pool Pool} object. This class stores names for those options.
 */
public class NetworkOptions {

    private NetworkOptions() {
        throw new RuntimeException("Do not instantiate");
    }

    /**
     * Pass this option if you wish to use an existing CloudStack network.
     */
    public static final String EXISTING_NETWORK_ID = "existingNetworkId";
}

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

package com.axemblr.provisionr.amazon;

public class ProcessVariables {

    private ProcessVariables() {
        /* singleton */
    }

    /**
     * Pool description object
     *
     * @see com.axemblr.provisionr.api.pool.Pool
     */
    public static final String POOL = "pool";

    /**
     * ID of the cached base image
     */
    public static final String CACHED_IMAGE_ID = "cachedImageId";

    /**
     * The reservation ID for a pool as String
     *
     * @see com.axemblr.provisionr.amazon.activities.RunOnDemandInstances
     */
    public static final String RESERVATION_ID = "reservationId";

    /**
     * String[] of instance IDs as returned by Amazon
     *
     * @see com.axemblr.provisionr.amazon.activities.RunOnDemandInstances
     */
    public static final String INSTANCES = "instanceIds";

    /**
     * Are all started instances running?
     *
     * @see com.axemblr.provisionr.amazon.activities.CheckAllInstancesAreRunning
     */
    public static final String ALL_INSTANCES_RUNNING = "allInstancesRunning";

    /**
     * Are all instances marked as terminated?
     *
     * @see com.axemblr.provisionr.amazon.activities.CheckAllInstancesAreTerminated
     */
    public static final String ALL_INSTANCES_TERMINATED = "allInstancesTerminated";

}

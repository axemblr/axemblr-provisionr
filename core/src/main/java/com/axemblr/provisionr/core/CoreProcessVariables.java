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

package com.axemblr.provisionr.core;

public class CoreProcessVariables {

    private CoreProcessVariables() {
        throw new RuntimeException("Should not instantiate");
    }

    /**
     * Pool configuration description
     *
     * @see com.axemblr.provisionr.api.pool.Pool
     */
    public static final String POOL = "pool";

    /**
     * Contains a list of machines that are running
     *
     * @see com.axemblr.provisionr.api.pool.Machine
     */
    public static final String MACHINES = "machines";

    /**
     * Name of the process variable that stores a {@link com.axemblr.provisionr.api.pool.Machine Machine} object.
     * Used inside the process to connect to that machine.
     */
    public static final String MACHINE = "machine";

    /**
     * Pool status stored as process variable
     * <p/>
     * This can be an arbitrary string. We will restrict the domain later on.
     */
    public static final String STATUS = "status";

    /**
     * Variable to store the pool business key. The key is passed as a process variable to all processes that take
     * part in setting up the pool.
     */
    public static final String POOL_BUSINESS_KEY = "poolBusinessKey";

}

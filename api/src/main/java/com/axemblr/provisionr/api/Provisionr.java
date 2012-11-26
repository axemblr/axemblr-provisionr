package com.axemblr.provisionr.api;

import com.axemblr.provisionr.api.pool.Pool;

public interface Provisionr {

    /**
     * Get unique ID for this provisionr
     */
    public String getId();

    /**
     * Start a provisioning process based on the pool description
     *
     * @param id   external process ID (e.g. job ID)
     * @param pool pool description
     * @return internal process ID
     */
    void startCreatePoolProcess(String id, Pool pool);

    /**
     * Destroy all the machines from the pool with that id
     *
     * @param id external pool ID (e.g. job ID)
     */
    void destroyPool(String id);
}

package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.Provisionr;

public class AmazonProvisionr implements Provisionr {

    public void init() {
        System.out.println("**** Amazon Provisionr (init)");
    }

    @Override
    public void createPool(String id, Pool pool) {
        System.out.println("**** Amazon Provisionr (createPool) id: " + id + " pool: " + pool);
    }

    @Override
    public void destroyPool(String id) {
        System.out.println("**** Amazon Provisionr (destroyPool) id: " + id);
    }
}

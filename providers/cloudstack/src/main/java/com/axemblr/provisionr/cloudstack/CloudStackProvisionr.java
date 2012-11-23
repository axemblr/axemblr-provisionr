package com.axemblr.provisionr.cloudstack;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;

public class CloudStackProvisionr implements Provisionr {

    public void init() {
        System.out.println("**** CloudStack (init)");
    }

    @Override
    public void startCreatePoolProcess(String id, Pool pool) {
        System.out.println("**** CloudStack (createPool) id: " + id + " pool: " + pool);
    }

    @Override
    public void destroyPool(String id) {
        System.out.println("**** CloudStack (destroyPool) id: " + id);
    }
}

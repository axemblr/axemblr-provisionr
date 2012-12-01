package com.axemblr.provisionr.cloudstack;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudStackProvisionr implements Provisionr {

    private static final Logger LOG = LoggerFactory.getLogger(CloudStackProvisionr.class);
    public static final String ID = "cloudstack";

    public void init() {
        LOG.info("**** CloudStack (init)");
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void startCreatePoolProcess(String id, Pool pool) {
        LOG.info("**** CloudStack (createPool) id: " + id + " pool: " + pool);
    }

    @Override
    public void destroyPool(String id) {
        LOG.info("**** CloudStack (destroyPool) id: " + id);
    }
}

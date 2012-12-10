package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazonProvisionr implements Provisionr {

    public static final Logger LOG = LoggerFactory.getLogger(AmazonProvisionr.class);

    public static final String ID = "amazon";
    public static final String PROCESS_KEY = "amazon";

    private final ProcessEngine processEngine;

    public AmazonProvisionr(ProcessEngine processEngine) {
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
        LOG.info("*** Amazon Provisionr constructor");
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void startPoolManagementProcess(String id, Pool pool) {
        LOG.info("**** Amazon Provisionr (createPool) id: " + id + " pool: " + pool);

        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put(ProcessVariables.POOL, pool);

        ProcessInstance instance = processEngine.getRuntimeService()
            .startProcessInstanceByKey(PROCESS_KEY, id, arguments);

        // TODO do something wih the ProcessInstance. maybe return?
    }

    @Override
    public void destroyPool(String id) {
        LOG.info("**** Amazon Provisionr (destroyPool) id: " + id);
    }
}

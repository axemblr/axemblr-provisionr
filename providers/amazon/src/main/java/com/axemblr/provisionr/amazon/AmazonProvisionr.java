package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;

public class AmazonProvisionr implements Provisionr {

    public static final String ID = "amazon";
    public static final String PROCESS_KEY = "amazon";

    private final ProcessEngine processEngine;

    public AmazonProvisionr(ProcessEngine processEngine) {
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
        System.err.println("*** Amazon Provisionr constructor");
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void startCreatePoolProcess(String id, Pool pool) {
        System.out.println("**** Amazon Provisionr (createPool) id: " + id + " pool: " + pool);

        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put("pool", pool);

        ProcessInstance instance = processEngine.getRuntimeService()
            .startProcessInstanceByKey(PROCESS_KEY, id, arguments);

        // TODO do something wih the ProcessInstance. maybe return?
    }

    @Override
    public void destroyPool(String id) {
        System.out.println("**** Amazon Provisionr (destroyPool) id: " + id);
    }
}

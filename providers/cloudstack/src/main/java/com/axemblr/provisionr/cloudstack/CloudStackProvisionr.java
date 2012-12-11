package com.axemblr.provisionr.cloudstack;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.collect.Maps;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudStackProvisionr implements Provisionr {

    private static final Logger LOG = LoggerFactory.getLogger(CloudStackProvisionr.class);
    public static final String ID = "cloudstack";
    /**
     * Process key must match the one in
     * axemblr-provisionr/providers/cloudstack/src/main/resources/OSGI-INF/activiti/cloudstack.bpmn20.xml
     */
    public static final String PROCESS_KEY = "cloudstack";

    private final ProcessEngine processEngine;

    public CloudStackProvisionr(ProcessEngine engine) {
        this.processEngine = engine;
        LOG.info("**** CloudStack (init)");
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void startPoolManagementProcess(String id, Pool pool) {
        LOG.info("**** CloudStack (startCreatePoolProcess) id: " + id + " pool: " + pool);
        //TODO: make sure the all information in the pool is valid - i.e. it will not make the cloud scream at us !!
        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put(ProcessVariables.POOL, pool);

        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, id, arguments);
    }

    @Override
    public void destroyPool(String id) {
        LOG.info("**** CloudStack (destroyPool) id: " + id);
    }
}

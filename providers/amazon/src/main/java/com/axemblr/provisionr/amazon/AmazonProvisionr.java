package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.amazon.config.DefaultProviderConfig;
import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Optional;
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
    private final Optional<Provider> defaultProvider;

    public AmazonProvisionr(ProcessEngine processEngine, DefaultProviderConfig defaultProviderConfig) {
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
        this.defaultProvider = defaultProviderConfig.createProvider();

        if (defaultProvider.isPresent()) {
            LOG.info("Default provider for AmazonProvisionr is {}", defaultProvider.get());
        } else {
            LOG.info("No default provider configured for AmazonProvisionr");
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Optional<Provider> getDefaultProvider() {
        return defaultProvider;
    }

    @Override
    public String startPoolManagementProcess(String businessKey, Pool pool) {
        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put(ProcessVariables.POOL, pool);

        ProcessInstance instance = processEngine.getRuntimeService()
            .startProcessInstanceByKey(PROCESS_KEY, businessKey, arguments);

        return instance.getProcessInstanceId();
    }

    @Override
    public void destroyPool(String businessKey) {
        LOG.info("**** Amazon Provisionr (destroyPool) id: " + businessKey);
    }
}

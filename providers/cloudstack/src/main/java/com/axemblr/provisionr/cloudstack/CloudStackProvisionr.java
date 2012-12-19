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

package com.axemblr.provisionr.cloudstack;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.axemblr.provisionr.core.ProvisionrSupport;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudStackProvisionr extends ProvisionrSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CloudStackProvisionr.class);
    public static final String ID = "cloudstack";
    /**
     * Process key must match the one in
     * axemblr-provisionr/providers/cloudstack/src/main/resources/OSGI-INF/activiti/cloudstack.bpmn20.xml
     */
    public static final String PROCESS_KEY = "cloudstack";

    private final ProcessEngine processEngine;
    private final Optional<Provider> defaultProvider;

    public CloudStackProvisionr(ProcessEngine processEngine, DefaultProviderConfig providerConfig) {
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
        this.defaultProvider = providerConfig.createProvider();

        if (defaultProvider.isPresent()) {
            LOG.info("Default provider for CloudStackProvisionr is {}", defaultProvider.get());
        } else {
            LOG.info("No default provider configured for CloudStackProvisionr");
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
        LOG.info("**** CloudStack (startCreatePoolProcess) id: " + businessKey + " pool: " + pool);
        //TODO: make sure the all information in the pool is valid - i.e. it will not make the cloud scream at us !!
        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put(CoreProcessVariables.POOL, pool);

        processEngine.getIdentityService().setAuthenticatedUserId("kermit");
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, businessKey, arguments);

        return instance.getProcessInstanceId();
    }

    @Override
    public void destroyPool(String businessKey) {
        LOG.info("**** CloudStack (destroyPool) id: " + businessKey);
        // TODO use triggerSignalEvent as needed
    }
}

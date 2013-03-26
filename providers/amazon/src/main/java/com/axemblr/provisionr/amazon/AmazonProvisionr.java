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

package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.amazon.config.DefaultProviderConfig;
import com.axemblr.provisionr.amazon.options.ProviderOptions;
import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.core.CoreConstants;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.axemblr.provisionr.core.CoreSignals;
import com.axemblr.provisionr.core.PoolStatus;
import com.axemblr.provisionr.core.ProvisionrSupport;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazonProvisionr extends ProvisionrSupport {

    public static final Logger LOG = LoggerFactory.getLogger(AmazonProvisionr.class);

    public static final String ID = "amazon";
    public static final String MANAGEMENT_PROCESS_KEY = "amazonPoolManagement";

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

        arguments.put(CoreProcessVariables.POOL, pool);
        arguments.put(CoreProcessVariables.PROVIDER, getId());
        arguments.put(CoreProcessVariables.POOL_BUSINESS_KEY, businessKey);
        arguments.put(CoreProcessVariables.BOOTSTRAP_TIMEOUT, 
                convertTimeoutToISO8601TimeDuration(pool.getBootstrapTimeInSeconds()));
        arguments.put(CoreProcessVariables.IS_CACHED_IMAGE, pool.getSoftware().isCachedImage());

        /* needed because the Activiti EL doesn't work as expected and properties can't be read from the pool. */
        arguments.put(ProcessVariables.SPOT_BID, pool.getProvider().getOption(ProviderOptions.SPOT_BID));

        /* Authenticate as kermit to make the process visible in the Explorer UI */
        processEngine.getIdentityService().setAuthenticatedUserId(CoreConstants.ACTIVITI_EXPLORER_DEFAULT_USER);

        ProcessInstance instance = processEngine.getRuntimeService()
            .startProcessInstanceByKey(MANAGEMENT_PROCESS_KEY, businessKey, arguments);

        return instance.getProcessInstanceId();
    }

    @Override
    public List<Machine> getMachines(String businessKey) {
        ProcessInstance instance = processEngine.getRuntimeService().createProcessInstanceQuery()
            .processInstanceBusinessKey(businessKey).singleResult();
        if (instance == null) {
            throw new NoSuchElementException("No active pool found with key: " + businessKey);
        }

        @SuppressWarnings("unchecked") List<Machine> machines = (List<Machine>) processEngine.getRuntimeService()
            .getVariable(instance.getId(), CoreProcessVariables.MACHINES);

        return Optional.fromNullable(machines).or(Collections.<Machine>emptyList());
    }

    @Override
    public String getStatus(String businessKey) {
        ProcessInstance instance = processEngine.getRuntimeService().createProcessInstanceQuery()
            .processInstanceBusinessKey(businessKey).singleResult();
        if (instance == null) {
            throw new NoSuchElementException("No active pool found with key: " + businessKey);
        }

        String status = (String) processEngine.getRuntimeService().getVariable(instance.getId(),
            CoreProcessVariables.STATUS);

        return Optional.fromNullable(status).or(PoolStatus.UNDEFINED);
    }

    @Override
    public void destroyPool(String businessKey) {
        triggerSignalEvent(processEngine, businessKey, CoreSignals.TERMINATE_POOL);
    }
}

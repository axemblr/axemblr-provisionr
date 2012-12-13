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

package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;
import static com.google.common.base.Preconditions.checkNotNull;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public abstract class AmazonActivity implements JavaDelegate {

    private final ProviderClientCache providerClientCache;

    protected AmazonActivity(ProviderClientCache providerClientCache) {
        this.providerClientCache = checkNotNull(providerClientCache, "providerClientCache is null");
    }

    /**
     * Amazon specific activity implementation
     *
     * @param client    Amazon client created using the pool provider
     * @param pool      Virtual machines pool description
     * @param execution Activiti execution context
     */
    public abstract void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception;

    /**
     * Wrap the abstract {@code execute} method with the logic that knows how to create the Amazon client
     */
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Pool pool = (Pool) execution.getVariable(CoreProcessVariables.POOL);
        checkNotNull(pool, "Please add the pool description as a process " +
            "variable with the name '%s'.", CoreProcessVariables.POOL);

        execute(providerClientCache.getUnchecked(pool.getProvider()), pool, execution);
    }
}

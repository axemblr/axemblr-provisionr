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

package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.commands.predicates.ProvisionrPredicates;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.NoSuchElementException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "provisionr", name = "destroy", description = "Destroy pool")
public class DestroyPoolCommand extends OsgiCommandSupport {

    @Option(name = "-k", aliases = "--key", description = "Pool key", required = true)
    private String businessKey;

    private final List<Provisionr> services;
    private final ProcessEngine processEngine;

    public DestroyPoolCommand(List<Provisionr> services, ProcessEngine processEngine) {
        this.services = checkNotNull(services, "services is null");
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        checkNotNull(businessKey, "pool business key is mandatory");

        ProcessInstance instance = processEngine.getRuntimeService().createProcessInstanceQuery()
            .processInstanceBusinessKey(businessKey).singleResult();
        checkNotNull(instance, "no pool found with key " + businessKey);

        String providerId = (String) processEngine.getRuntimeService()
            .getVariable(instance.getId(), CoreProcessVariables.PROVIDER);
        checkNotNull(providerId, "the process instance has no provider ID");

        Optional<Provisionr> service = Iterables.tryFind(services, ProvisionrPredicates.withId(providerId));

        if (service.isPresent()) {
            service.get().destroyPool(businessKey);
        } else {
            throw new NoSuchElementException("No provisioning service found with id: " + providerId);
        }

        return null;
    }

    @VisibleForTesting
    void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}

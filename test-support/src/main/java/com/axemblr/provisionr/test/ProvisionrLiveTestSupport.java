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

package com.axemblr.provisionr.test;

import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.provider.ProviderBuilder;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ProvisionrLiveTestSupport {

    @Inject
    protected BundleContext bundleContext;

    protected final String provisionrId;

    /**
     * Supply the provisionrId to acquire proper credentials from System Properties.
     *
     * @param provisionrId
     */
    public ProvisionrLiveTestSupport(String provisionrId) {
        this.provisionrId = checkNotNull(provisionrId, "provisionrId is null");
    }

    /**
     * Retrieve a reference to an OSGi service using the class name
     */
    protected <T> T getOsgiService(Class<T> klass, int timeoutInMilliseconds) throws InterruptedException {
        ServiceTracker<T, T> tracker = new ServiceTracker<T, T>(bundleContext,
            klass.getCanonicalName(), null);
        tracker.open(true);

        try {
            return checkNotNull(tracker.waitForService(timeoutInMilliseconds), "OSGi Service not available "
                + klass.getCanonicalName());
        } finally {
            tracker.close();
        }
    }

    /**
     * Collect the provider connection details from system properties
     */
    protected ProviderBuilder collectProviderCredentialsFromSystemProperties() {
        return Provider.builder().id(provisionrId)
            .accessKey(getProviderProperty("accessKey"))
            .secretKey(getProviderProperty("secretKey"))
            .endpoint(Optional.fromNullable(getProviderProperty("endpoint")));
    }

    /**
     * Get a provider configuration property from system properties
     */
    protected String getProviderProperty(String property) {
        return System.getProperty(String.format("test.%s.provider.%s", provisionrId, property));
    }

    /**
     * @see #getProviderProperty
     */
    protected String getProviderProperty(String property, String defaultValue) {
        return Optional.fromNullable(getProviderProperty(property)).or(defaultValue);
    }

    public String getResourceAsString(String resource) throws IOException {
        return Resources.toString(Resources.getResource(resource), Charsets.UTF_8);
    }

    /**
     * Must be called inside a test method to be able to access OSGi infrastructure,
     *
     * @param processKey
     * @throws InterruptedException
     */
    public void waitForProcessDeployment(String processKey) throws InterruptedException, TimeoutException {
        ProcessEngine engine = getOsgiService(ProcessEngine.class, 5000);
        int iteration = 0;
        while (iteration < 5) {
            ProcessDefinition definition = engine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(processKey).singleResult();
            if (definition != null) {
                break;
            }
            iteration++;
            TimeUnit.MILLISECONDS.sleep(500);
        }
        if (iteration == 5) {
            throw new TimeoutException("No process found with key: " + processKey);
        }
    }

    public void waitForProcessEnd(final String processId) throws InterruptedException {
        while (processNotEnded(processId)) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private boolean processNotEnded(final String processId) throws InterruptedException {
        ProcessInstance localInstance = getProcessInstanceById(processId);
        return localInstance != null && !localInstance.isEnded();
    }

    private ProcessInstance getProcessInstanceById(final String processId) throws InterruptedException {
        ProcessEngine engine = getOsgiService(ProcessEngine.class, 5000);
        return engine.getRuntimeService().createProcessInstanceQuery()
            .processInstanceId(processId)
            .singleResult();
    }
}

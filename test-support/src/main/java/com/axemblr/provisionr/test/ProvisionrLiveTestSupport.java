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
import com.google.common.base.Stopwatch;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProvisionrLiveTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(ProvisionrLiveTestSupport.class);

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
        ServiceTracker tracker = new ServiceTracker(bundleContext,
            klass.getCanonicalName(), null);
        tracker.open(true);

        try {
            return (T) checkNotNull(tracker.waitForService(timeoutInMilliseconds),
                "OSGi Service not available " + klass.getCanonicalName());
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

    public void waitForProcessEnd(String processInstanceId) throws Exception {
        waitForProcessEnd(processInstanceId, 60000 /* milliseconds */);
    }

    public void waitForProcessEnd(final String processInstanceId, int timeoutInMilliseconds) throws Exception {
        Stopwatch stopwatch = new Stopwatch().start();
        while (isProcessNotEnded(processInstanceId)) {
            if (stopwatch.elapsedMillis() > timeoutInMilliseconds) {
                throw new TimeoutException(String.format("Process %s not ended in %d milliseconds.",
                    processInstanceId, timeoutInMilliseconds));
            }
            LOG.info(String.format("Process instance %s not ended. Waiting 1s.", processInstanceId));
            TimeUnit.SECONDS.sleep(1);
        }
        LOG.info(String.format("Process instance %s ended as expected in less than %d milliseconds",
            processInstanceId, timeoutInMilliseconds));
    }

    private boolean isProcessNotEnded(final String processInstanceId) throws InterruptedException {
        ProcessInstance localInstance = getProcessInstanceById(processInstanceId);
        return localInstance != null && !localInstance.isEnded();
    }

    private ProcessInstance getProcessInstanceById(final String processInstanceId) throws InterruptedException {
        ProcessEngine engine = getOsgiService(ProcessEngine.class, 5000);
        return engine.getRuntimeService().createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
    }
}

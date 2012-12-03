package com.axemblr.provisionr.test;

import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.provider.ProviderBuilder;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.inject.Inject;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ProvisionrLiveTestSupport {

    @Inject
    protected BundleContext bundleContext;

    protected final String provisionrId;

    public ProvisionrLiveTestSupport(String provisionrId) {
        this.provisionrId = checkNotNull(provisionrId, "provisionrId is null");
    }

    /**
     * Retrieve a reference to an OSGi service using the class name
     */
    protected <T> T getOsgiService(Class<T> klass, int timeout) throws InterruptedException {
        ServiceTracker<T, T> tracker = new ServiceTracker<T, T>(bundleContext,
            klass.getCanonicalName(), null);
        tracker.open(true);

        try {
            return tracker.waitForService(timeout);
        } finally {
            tracker.close();
        }
    }

    /**
     * Collect the provider connection details from system properties
     */
    protected ProviderBuilder collectProviderCredentialsFromSystemProperties() {
        return Provider.builder().id(provisionrId).accessKey(getProviderProperty("accessKey"))
            .secretKey(getProviderProperty("accessKey")).endpoint(getProviderProperty("endpoint"));
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
}

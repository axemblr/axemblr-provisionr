package com.axemblr.provisionr.test;

import javax.inject.Inject;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ProvisionrLiveTestSupport {

    @Inject
    protected BundleContext bundleContext;

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
}

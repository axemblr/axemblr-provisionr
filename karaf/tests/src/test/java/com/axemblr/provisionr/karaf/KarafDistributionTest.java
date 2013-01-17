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

package com.axemblr.provisionr.karaf;

import com.axemblr.provisionr.api.Provisionr;
import static com.axemblr.provisionr.test.KarafTests.projectVersionAsSystemProperty;
import static com.axemblr.provisionr.test.KarafTests.useDefaultKarafAsInProjectWithJunitBundles;
import com.google.common.base.Stopwatch;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Axemblr Provisionr Feature installation in Apache Karaf.
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class KarafDistributionTest {

    private static final Logger LOG = LoggerFactory.getLogger(KarafDistributionTest.class);

    public static final String ACTIVITI_EXPLORER_URL = "http://localhost:8181/activiti-explorer/";

    public static final String LOCALHOST = "localhost";

    public static final int DEFAULT_JETTY_PORT = 8181;
    public static final int TIMEOUT_IN_MILLISECONDS = 1000;

    public static final int EXPECTED_NUMBER_OF_PROVISIONR_SERVICES = 2; /* amazon & cloudstack */

    @Inject
    private FeaturesService features;

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] configuration() throws Exception {
        return new Option[]{
            useDefaultKarafAsInProjectWithJunitBundles(),
            projectVersionAsSystemProperty(),
            systemProperty("jclouds.version").value(MavenUtils
                .getArtifactVersion("org.jclouds.karaf", "jclouds-karaf"))
        };
    }

    @Test
    public void testAllFeaturesStartAsExpected() throws Exception {
        features.addRepository(getProvisionrFeaturesUrl());
        features.addRepository(getJcloudsKarafFeaturesUrl());

        features.installFeature("axemblr-provisionr");
        assertFeatureInstalled("axemblr-provisionr");

        assertAllBundlesAreActive();

        assertJettyStartsInLessThan(5000 /* milliseconds */);
        assertProvisionrServicesAreStartedInLessThan(5000 /* milliseconds */);

        assertActivitiExplorerIsRunningInLessThan(30000 /* milliseconds */);
    }

    private void assertActivitiExplorerIsRunningInLessThan(int timeoutInMilliseconds) throws InterruptedException {
        final Stopwatch stopwatch = new Stopwatch().start();
        while (true) {
            if (stopwatch.elapsedMillis() > timeoutInMilliseconds) {
                fail(String.format("Activiti Explorer did not start at %s in less than %d milliseconds",
                    ACTIVITI_EXPLORER_URL, timeoutInMilliseconds));
            }

            try {
                String content = CharStreams.toString(
                    new InputStreamReader(new URL(ACTIVITI_EXPLORER_URL).openStream()));

                assertTrue(content.contains("Vaadin"));
                break;  /* test completed as expected */

            } catch (Exception e) {
                LOG.info(String.format("Activiti Explorer not started yet (%s). Trying again in 10s.", e.getMessage()));
                TimeUnit.SECONDS.sleep(10);
            }
        }
    }

    private <T> void assertProvisionrServicesAreStartedInLessThan(int timeoutInMilliseconds) throws InterruptedException {
        final ServiceTracker<T, T> tracker = new ServiceTracker<T, T>(bundleContext,
            Provisionr.class.getCanonicalName(), null);
        tracker.open(true);
        try {
            final Stopwatch stopwatch = new Stopwatch().start();
            final int expectedCount = EXPECTED_NUMBER_OF_PROVISIONR_SERVICES;

            while (true) {
                Object[] services = tracker.getServices();
                if (services == null || services.length < expectedCount) {
                    final int actualCount = (services == null) ? 0 : services.length;
                    if (stopwatch.elapsedMillis() > timeoutInMilliseconds) {
                        fail(String.format("Expected to find %d Provisionr services. Found only %d in %d milliseconds",
                            expectedCount, actualCount, timeoutInMilliseconds));
                    }

                    LOG.info(String.format("Found %d services implementing %s. Trying again in 1s.",
                        actualCount, Provisionr.class.getCanonicalName()));
                    TimeUnit.SECONDS.sleep(1);

                } else if (services.length > expectedCount) {
                    fail(String.format("Expected to find %d services implementing %s. Found %d (more than expected).",
                        expectedCount, Provisionr.class.getCanonicalName(), services.length));

                } else if (services.length == expectedCount) {
                    break;  /* done - the test was successful */
                }
            }

        } finally {
            tracker.close();
        }
    }

    private URI getJcloudsKarafFeaturesUrl() {
        return URI.create(maven("org.jclouds.karaf", "jclouds-karaf")
            .version(System.getProperty("jclouds.version"))
            .classifier("features")
            .type("xml")
            .getURL());
    }

    private URI getProvisionrFeaturesUrl() {
        return URI.create(maven("com.axemblr.provisionr", "provisionr-features")
            .version(System.getProperty("project.version"))
            .classifier("features")
            .type("xml")
            .getURL());
    }

    private void assertAllBundlesAreActive() {
        for (Bundle bundle : bundleContext.getBundles()) {
            // skip fragments, they can't be started
            if (bundle.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
                assertEquals("Bundle " + bundle.getSymbolicName() + " is not active",
                    Bundle.ACTIVE, bundle.getState());
            }
        }
    }

    private void assertFeatureInstalled(String featureName) throws Exception {
        Feature feature = features.getFeature(featureName);
        assertTrue("Feature " + featureName + " should be installed", features.isInstalled(feature));
    }

    private void assertJettyStartsInLessThan(int timeoutInMilliseconds) throws InterruptedException {
        Stopwatch stopwatch = new Stopwatch().start();
        while (!isPortOpen(LOCALHOST, DEFAULT_JETTY_PORT)) {
            if (stopwatch.elapsedMillis() > timeoutInMilliseconds) {
                fail(String.format("Jetty did not start listening on port %d in less than %d milliseconds",
                    DEFAULT_JETTY_PORT, timeoutInMilliseconds));
            }

            LOG.info("Waiting 1s for Jetty to listen on port 8181.");
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private boolean isPortOpen(String hostname, int port) {
        InetSocketAddress socketAddress = new InetSocketAddress(hostname, port);

        Socket socket = null;
        try {
            socket = new Socket();
            socket.setReuseAddress(false);
            socket.setSoLinger(false, 1);
            socket.setSoTimeout(TIMEOUT_IN_MILLISECONDS);
            socket.connect(socketAddress, TIMEOUT_IN_MILLISECONDS);

        } catch (IOException e) {
            return false;

        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioe) {
                    // no work to do
                }
            }
        }
        return true;
    }
}

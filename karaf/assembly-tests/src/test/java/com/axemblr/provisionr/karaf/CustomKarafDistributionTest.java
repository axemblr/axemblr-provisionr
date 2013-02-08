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
import com.axemblr.provisionr.core.templates.PoolTemplate;
import static com.axemblr.provisionr.test.KarafTests.getKarafVersionAsInProject;
import com.google.common.base.Stopwatch;
import com.google.common.io.CharStreams;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.keepRuntimeFolder;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import org.apache.karaf.tooling.exam.options.LogLevelOption;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run a set of tests on the custom Karaf distribution
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class CustomKarafDistributionTest {

    private static final Logger LOG = LoggerFactory.getLogger(CustomKarafDistributionTest.class);

    public static final String ACTIVITI_EXPLORER_URL = "http://localhost:8181/activiti-explorer/";

    public static final String LOCALHOST = "localhost";

    public static final int DEFAULT_JETTY_PORT = 8181;
    public static final int TIMEOUT_IN_MILLISECONDS = 1000;

    /**
     * We are only starting Amazon by default. The support for cloudstack is not ready yet.
     */
    public static final int EXPECTED_NUMBER_OF_PROVISIONR_SERVICES = 1;

    /**
     * We only register two pool templates by default through the provisionr-core bundle
     */
    public static final int EXPECTED_NUMBER_OF_POOL_TEMPLATES = 2;

    @Inject
    private FeaturesService features;

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] configuration() throws Exception {
        MavenArtifactUrlReference distributionUrl = maven().groupId("com.axemblr.provisionr")
            .artifactId("provisionr-assembly").versionAsInProject().type("tar.gz");

        return new Option[]{
            karafDistributionConfiguration()
                .frameworkUrl(distributionUrl)
                .karafVersion(getKarafVersionAsInProject())
                .name("Axemblr Provisionr")
                .unpackDirectory(new File("target/exam")),
            keepRuntimeFolder(),
            junitBundles(),
            logLevel(LogLevelOption.LogLevel.INFO)
        };
    }

    @Test
    public void testAllFeaturesStartAsExpected() throws Exception {
        assertFeatureInstalled("axemblr-provisionr-all");

        assertAllBundlesAreActive();

        assertJettyStartsInLessThan(5000 /* milliseconds */);

        assertProvisionrServicesAreStartedInLessThan(5000 /* milliseconds */);
        assertPoolTemplatesAreRegisteredInLessThan(5000 /* milliseconds */);

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

    private void assertProvisionrServicesAreStartedInLessThan(int timeoutInMilliseconds) throws Exception {
        assertServicesAreStartedInLessThan(Provisionr.class,
            EXPECTED_NUMBER_OF_PROVISIONR_SERVICES, timeoutInMilliseconds);
    }

    private void assertPoolTemplatesAreRegisteredInLessThan(int timeoutInMilliseconds) throws Exception {
        assertServicesAreStartedInLessThan(PoolTemplate.class,
            EXPECTED_NUMBER_OF_POOL_TEMPLATES, timeoutInMilliseconds);

    }

    private void assertServicesAreStartedInLessThan(
        Class<?> klass, int expectedCardinality, int timeoutInMilliseconds
    ) throws Exception {
        final ServiceTracker tracker = new ServiceTracker(bundleContext, klass.getName(), null);
        tracker.open(true);
        try {
            final Stopwatch stopwatch = new Stopwatch().start();

            while (true) {
                Object[] services = tracker.getServices();
                if (services == null || services.length < expectedCardinality) {
                    final int actualCount = (services == null) ? 0 : services.length;
                    if (stopwatch.elapsedMillis() > timeoutInMilliseconds) {
                        fail(String.format("Expected to find %d %s services. Found only %d in %d milliseconds",
                            expectedCardinality, klass.getSimpleName(), actualCount, timeoutInMilliseconds));
                    }

                    LOG.info(String.format("Found %d services implementing %s. Trying again in 1s.",
                        actualCount, klass.getName()));
                    TimeUnit.SECONDS.sleep(1);

                } else if (services.length > expectedCardinality) {
                    fail(String.format("Expected to find %d services implementing %s. Found %d (more than expected).",
                        expectedCardinality, klass.getName(), services.length));

                } else if (services.length == expectedCardinality) {
                    break;  /* done - the test was successful */
                }
            }

        } finally {
            tracker.close();
        }
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

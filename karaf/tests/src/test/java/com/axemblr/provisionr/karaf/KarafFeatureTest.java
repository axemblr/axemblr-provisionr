package com.axemblr.provisionr.karaf;

import com.axemblr.provisionr.test.KarafTests;
import java.net.URI;
import javax.inject.Inject;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import org.apache.karaf.tooling.exam.options.LogLevelOption;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Test Axemblr Provisionr Feature installation in Apache Karaf.
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class KarafFeatureTest {

    @Inject
    private FeaturesService features;

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] configuration() throws Exception {
        return new Option[]{
            KarafTests.useDefaultKarafAsInProject(),
            junitBundles(),
            logLevel(LogLevelOption.LogLevel.INFO),
            KarafTests.projectVersionAsSystemProperty(),
        };
    }

    @Test
    public void shouldInstallAllFeatures() throws Exception {
        String url = maven("com.axemblr.provisionr", "provisionr-features")
            .version(System.getProperty("project.version"))
            .classifier("features")
            .type("xml")
            .getURL();

        features.addRepository(new URI(url));
        features.installFeature("axemblr-provisionr");

        assertInstalled("activiti");
        assertInstalled("axemblr-provisionr");

        for (Bundle bundle : bundleContext.getBundles()) {
            assertEquals("Bundle " + bundle.getSymbolicName() + " is not active",
                Bundle.ACTIVE, bundle.getState());
        }

        // TODO check services are published as expected
    }

    private void assertInstalled(String featureName) throws Exception {
        Feature feature = features.getFeature(featureName);
        assertTrue("Feature " + featureName + " should be installed", features.isInstalled(feature));
    }
}

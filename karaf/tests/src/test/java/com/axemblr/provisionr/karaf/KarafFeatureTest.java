package com.axemblr.provisionr.karaf;

import static com.axemblr.provisionr.test.KarafTests.projectVersionAsSystemProperty;
import static com.axemblr.provisionr.test.KarafTests.useDefaultKarafAsInProjectWithJunitBundles;
import java.net.URI;
import javax.inject.Inject;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import org.osgi.framework.wiring.BundleRevision;

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
            useDefaultKarafAsInProjectWithJunitBundles(),
            projectVersionAsSystemProperty(),
            systemProperty("jclouds.version").value(MavenUtils.getArtifactVersion("org.jclouds.karaf", "jclouds-karaf"))
        };
    }

    @Test
    public void shouldInstallAllFeatures() throws Exception {
        String url = maven("com.axemblr.provisionr", "provisionr-features")
            .version(System.getProperty("project.version"))
            .classifier("features")
            .type("xml")
            .getURL();

        String jcloudsFeatureUrl = maven("org.jclouds.karaf", "jclouds-karaf")
            .version(System.getProperty("jclouds.version"))
            .classifier("features")
            .type("xml")
            .getURL();

        features.addRepository(new URI(url));
        features.addRepository(new URI(jcloudsFeatureUrl));
        features.installFeature("axemblr-provisionr");

        assertInstalled("activiti");
        assertInstalled("jclouds-api-cloudstack");
        assertInstalled("axemblr-provisionr");

        for (Bundle bundle : bundleContext.getBundles()) {
            // skip fragments, they can't be started
            if (bundle.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
                assertEquals("Bundle " + bundle.getSymbolicName() + " is not active",
                    Bundle.ACTIVE, bundle.getState());
            }
        }

        // TODO check services are published as expected
    }

    private void assertInstalled(String featureName) throws Exception {
        Feature feature = features.getFeature(featureName);
        assertTrue("Feature " + featureName + " should be installed", features.isInstalled(feature));
    }
}

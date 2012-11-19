package com.axemblr.provisionr.karaf;

import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import org.apache.karaf.tooling.exam.options.LogLevelOption;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Test Axemblr Provisionr Feature instalation in Apache Karaf.
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class KarafFeatureTest {

  private static Logger LOG = LoggerFactory.getLogger(KarafFeatureTest.class);

  @Inject
  private FeaturesService features;

  private String featuresVersion;

  @Configuration
  public static Option[] configuration() throws Exception {
    MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf")
        .artifactId("apache-karaf")
        .version("2.3.0")
        .type("tar.gz");

    String provisionrVersion = MavenUtils.getArtifactVersion("com.axemblr.provisionr", "provisionr-features");

    return new Option[]{
        karafDistributionConfiguration().frameworkUrl(karafUrl).karafVersion("2.3.0").name("Apache Karaf")
            .unpackDirectory(new File("target/exam")),
        logLevel(LogLevelOption.LogLevel.INFO),
        // use system property to provide project version for tests
        systemProperty("provisionr-features").value(provisionrVersion)
    };
  }

  @Before
  public void setUp() {
    featuresVersion = System.getProperty("provisionr-features");
  }

  @Test
  public void shouldInstallAllFeatures() throws Exception {
    String url = maven("com.axemblr.provisionr", "provisionr-features")
        .version(featuresVersion)
        .classifier("features")
        .type("xml")
        .getURL();

    features.addRepository(new URI(url));
    features.installFeature("axemblr-provisionr");

    List<String> installed = new ArrayList<String>();
    for (Feature feature : features.listInstalledFeatures()) {
      installed.add(feature.getName());
    }

    assertIstalled("axemblr-provisionr");
  }

  private void assertIstalled(String featureName) throws Exception {
    Feature feature = features.getFeature(featureName);
    assertTrue("Feature " + featureName + " should be installed", features.isInstalled(feature));
  }
}

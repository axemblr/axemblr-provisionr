package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import java.io.File;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.karaf.features.FeaturesService;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import org.apache.karaf.tooling.exam.options.LogLevelOption;
import static org.junit.Assert.assertNotNull;
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
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class AmazonProvisionrLiveTest {

    @Inject
    private FeaturesService features;

    @Inject
    private BundleContext bundleContext;

    private String featuresVersion;

    @Configuration
    public static Option[] configuration() throws Exception {
        String karafVersion = MavenUtils.asInProject().getVersion("org.apache.karaf", "apache-karaf");
        MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf")
            .artifactId("apache-karaf")
            .version(karafVersion)
            .type("tar.gz");

        String provisionrVersion = MavenUtils.asInProject()
            .getVersion("com.axemblr.provisionr", "provisionr-features");

        return new Option[]{
            karafDistributionConfiguration()
                .frameworkUrl(karafUrl)
                .karafVersion(karafVersion)
                .name("Apache Karaf")
                .unpackDirectory(new File("target/exam")),
            logLevel(LogLevelOption.LogLevel.INFO),
            // use system property to provide project version for tests
            systemProperty("provisionr-features-version").value(provisionrVersion),
            systemProperty("test.amazon.provider.accessKey")
                .value(System.getProperty("test.amazon.provider.accessKey"))
        };
    }

    @Before
    public void setUp() throws Exception {
        featuresVersion = System.getProperty("provisionr-features-version");

        String url = maven("com.axemblr.provisionr", "provisionr-features")
            .version(featuresVersion)
            .classifier("features")
            .type("xml")
            .getURL();

        features.addRepository(new URI(url));
        features.installFeature("axemblr-provisionr-amazon");

    }

    @Test
    public void startProvisioningProcess() throws InvalidSyntaxException, InterruptedException {
        ServiceTracker tracker = new ServiceTracker(bundleContext, Provisionr.class.getCanonicalName(), null);
        tracker.open(true);

        Provisionr provisionr = (Provisionr) tracker.waitForService(5000);
        tracker.close();
        assertNotNull(provisionr);

        final String accessKey = System.getProperty("test.amazon.provider.accessKey");
        assertNotNull(accessKey);

        Provider provider = Provider.builder().id("aws-ec2")
            .accessKey(accessKey)
            .createProvider();
        Pool pool = Pool.builder().provider(provider).createPool();

        provisionr.startCreatePoolProcess(UUID.randomUUID().toString(), pool);

        TimeUnit.SECONDS.sleep(5);
    }
}

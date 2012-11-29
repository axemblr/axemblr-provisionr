package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.test.KarafTests;
import static com.axemblr.provisionr.test.KarafTests.systemPropertyPassThrough;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.karaf.features.FeaturesService;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import org.apache.karaf.tooling.exam.options.LogLevelOption;
import static org.junit.Assert.assertNotNull;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class AmazonProvisionrLiveTest {

    @Inject
    private FeaturesService features;

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] configuration() throws Exception {
        return new Option[]{
            KarafTests.useDefaultKarafAsInProject(),
            junitBundles(),
            KarafTests.projectVersionAsSystemProperty(),
            logLevel(LogLevelOption.LogLevel.INFO),

            systemPropertyPassThrough("test.amazon.provider.accessKey"),
            systemPropertyPassThrough("test.amazon.provider.secretKey"),
            systemPropertyPassThrough("test.amazon.provider.region")
        };
    }

    @Before
    public void setUp() throws Exception {
        // TODO we need to find a way to move this to configuration()
        String projectVersion = System.getProperty("project.version");

        String url = maven("com.axemblr.provisionr", "provisionr-features")
            .version(projectVersion)
            .classifier("features")
            .type("xml")
            .getURL();

        features.addRepository(new URI(url));
        features.installFeature("axemblr-provisionr-amazon");
    }

    @Test
    public void startProvisioningProcess() throws Exception {
        Provisionr provisionr = (Provisionr) waitForProvisionrService(5000);
        Provider provider = (Provider) loadProviderFromSystemProperties();
        Pool pool = Pool.builder().provider(provider).createPool();

        provisionr.startCreatePoolProcess(UUID.randomUUID().toString(), pool);
        TimeUnit.SECONDS.sleep(5);  // TODO replace with wait on process to finish
    }

    private Object waitForProvisionrService(int timeout) throws InterruptedException {
        ServiceTracker tracker = new ServiceTracker(bundleContext, Provisionr.class.getCanonicalName(), null);
        tracker.open(true);

        Provisionr provisionr = (Provisionr) tracker.waitForService(timeout);
        tracker.close();
        assertNotNull(provisionr);

        return provisionr;
    }

    private Object loadProviderFromSystemProperties() {
        final String accessKey = System.getProperty("test.amazon.provider.accessKey");
        final String secretKey = System.getProperty("test.amazon.provider.secretKey");
        final String region = System.getProperty("test.amazon.provider.region");

        Assume.assumeNotNull(accessKey, secretKey, region);

        return Provider.builder()
            .id("amazon")
            .accessKey(accessKey)
            .secretKey(secretKey)
            .option("region", region)
            .createProvider();
    }
}

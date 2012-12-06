package com.axemblr.provisionr.cloudstack;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.software.Software;
import static com.axemblr.provisionr.test.KarafTests.installProvisionrFeatures;
import static com.axemblr.provisionr.test.KarafTests.installProvisionrTestSupportBundle;
import static com.axemblr.provisionr.test.KarafTests.passThroughAllSystemPropertiesWithPrefix;
import static com.axemblr.provisionr.test.KarafTests.useDefaultKarafAsInProjectWithJunitBundles;
import com.axemblr.provisionr.test.ProvisionrLiveTestSupport;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.scanFeatures;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class CloudStackProvisionrLiveTest extends ProvisionrLiveTestSupport {

    public CloudStackProvisionrLiveTest() {
        super(CloudStackProvisionr.ID);
    }

    @Configuration
    public Option[] configuration() throws Exception {
        return new Option[]{
            useDefaultKarafAsInProjectWithJunitBundles(),
            passThroughAllSystemPropertiesWithPrefix("test.cloudstack."),
            scanFeatures(maven().groupId("org.jclouds.karaf").artifactId("jclouds-karaf")
                .type("xml").classifier("features").versionAsInProject(), "jclouds-api-cloudstack"),
            installProvisionrFeatures("axemblr-provisionr-cloudstack"),
            installProvisionrTestSupportBundle()
        };
    }

    @Test
    public void startProvisioningProcess() throws Exception {
        // TODO: We need to wait for the process to be registered before we call it. Replace with something smarter
        TimeUnit.SECONDS.sleep(5);
        Provisionr provisionr = getOsgiService(Provisionr.class, 5000);

        final Provider provider = collectProviderCredentialsFromSystemProperties()
            // TODO: get more options as needed for CloudStack
            .createProvider();

        final Network network = Network.builder()
            .addRules(Rule.builder().anySource().tcp().port(22).createRule())
            .createNetwork();

        final Software software = Software.builder().baseOperatingSystem("ubuntu-10.04")
            .packages("nginx").createSoftware();

        final AdminAccess adminAccess = AdminAccess.builder().username("admin").publicKey("ssh-rsa ")
            .privateKey("-----BEGIN RSA PRIVATE KEY-----").createAdminAccess();

        final Hardware hardware = Hardware.builder().type("offering").createHardware();

        final Pool pool = Pool.builder().network(network).provider(provider).adminAccess(adminAccess)
            .software(software).hardware(hardware).minSize(1).expectedSize(1).createPool();

        provisionr.startCreatePoolProcess(UUID.randomUUID().toString(), pool);
        TimeUnit.SECONDS.sleep(10);  // TODO replace with wait on process to finish
    }
}

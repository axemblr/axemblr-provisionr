package com.axemblr.provisionr.cloudstack;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import static com.axemblr.provisionr.test.KarafTests.installProvisionrFeatures;
import static com.axemblr.provisionr.test.KarafTests.installProvisionrTestSupportBundle;
import static com.axemblr.provisionr.test.KarafTests.passThroughAllSystemPropertiesWithPrefix;
import static com.axemblr.provisionr.test.KarafTests.useDefaultKarafAsInProjectWithJunitBundles;
import com.axemblr.provisionr.test.ProvisionrLiveTestSupport;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class CloudStackProvisionrLiveTest extends ProvisionrLiveTestSupport {

    @Configuration
    public Option[] configuration() throws Exception {
        return new Option[]{
            useDefaultKarafAsInProjectWithJunitBundles(),
            passThroughAllSystemPropertiesWithPrefix("test.cloudstack."),
            installProvisionrFeatures("axemblr-provisionr-cloudstack"),
            installProvisionrTestSupportBundle()
        };
    }

    @Test
    public void startProvisioningProcess() throws Exception {
        Provisionr provisionr = getOsgiService(Provisionr.class, 5000);

        Provider provider = getProviderFromSystemProperties("cloudstack");
        Pool pool = Pool.builder().provider(provider).createPool();

        provisionr.startCreatePoolProcess(UUID.randomUUID().toString(), pool);
        TimeUnit.SECONDS.sleep(5);  // TODO replace with wait on process to finish
    }

    /**
     * Get the provider connection details from system properties
     */
    protected Provider getProviderFromSystemProperties(String id) {
        final String accessKey = System.getProperty("test." + id + ".provider.accessKey");
        final String secretKey = System.getProperty("test." + id + ".provider.secretKey");
//        final String region = System.getProperty("test." + id + ".provider.region");

        return Provider.builder().id(id).accessKey(accessKey).secretKey(secretKey).createProvider();
    }
}

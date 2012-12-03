package com.axemblr.provisionr.amazon;

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
public class AmazonProvisionrLiveTest extends ProvisionrLiveTestSupport {

    public AmazonProvisionrLiveTest() {
        super(AmazonProvisionr.ID);
    }

    @Configuration
    public Option[] configuration() throws Exception {
        return new Option[]{
            useDefaultKarafAsInProjectWithJunitBundles(),
            passThroughAllSystemPropertiesWithPrefix("test.amazon."),
            installProvisionrFeatures("axemblr-provisionr-amazon"),
            installProvisionrTestSupportBundle()
        };
    }

    @Test
    public void startProvisioningProcess() throws Exception {
        Provisionr provisionr = getOsgiService(Provisionr.class, 5000);

        Provider provider = collectProviderCredentialsFromSystemProperties()
            .option("region", getProviderProperty("region", "us-east-1"))
            .createProvider();

        Pool pool = Pool.builder().provider(provider).createPool();

        provisionr.startCreatePoolProcess(UUID.randomUUID().toString(), pool);
        TimeUnit.SECONDS.sleep(5);  // TODO replace with wait on process to finish
    }
}

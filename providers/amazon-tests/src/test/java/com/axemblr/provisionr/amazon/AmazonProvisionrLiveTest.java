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

package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Protocol;
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
        waitForProcessDeployment(AmazonProvisionr.PROCESS_KEY);

        final Provisionr provisionr = getOsgiService(Provisionr.class, 5000);

        final Provider provider = collectProviderCredentialsFromSystemProperties()
            .option("region", getProviderProperty("region", "us-east-1"))
            .createProvider();

        final Network network = Network.builder().addRules(
            Rule.builder().anySource().icmp().createRule(),
            Rule.builder().anySource().port(22).protocol(Protocol.TCP).createRule(),
            Rule.builder().anySource().port(80).protocol(Protocol.TCP).createRule()
        ).createNetwork();

        final Hardware hardware = Hardware.builder().type("t1.micro").createHardware();

        final Software software = Software.builder().baseOperatingSystem("ubuntu-10.04")
            .packages("nginx").createSoftware();

        final AdminAccess adminAccess = AdminAccess.builder().asCurrentUser().createAdminAccess();

        final Pool pool = Pool.builder().provider(provider).network(network).adminAccess(adminAccess)
            .software(software).hardware(hardware).minSize(2).expectedSize(2).createPool();

        final String businessKey = "j-" + UUID.randomUUID().toString();
        provisionr.startPoolManagementProcess(businessKey, pool);

        TimeUnit.SECONDS.sleep(60);  // TODO replace with wait on process to finish

        provisionr.destroyPool(businessKey);

        TimeUnit.SECONDS.sleep(60);


        // TODO: get the list of machines and check that nginx is listening on port 80
    }
}

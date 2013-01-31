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

import com.axemblr.provisionr.amazon.options.ProviderOptions;
import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Protocol;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.core.PoolStatus;
import com.axemblr.provisionr.core.Ssh;
import com.axemblr.provisionr.core.templates.JenkinsTemplate;
import static com.axemblr.provisionr.test.KarafTests.installProvisionrFeatures;
import static com.axemblr.provisionr.test.KarafTests.installProvisionrTestSupportBundle;
import static com.axemblr.provisionr.test.KarafTests.passThroughAllSystemPropertiesWithPrefix;
import static com.axemblr.provisionr.test.KarafTests.useDefaultKarafAsInProjectWithJunitBundles;
import com.axemblr.provisionr.test.ProvisionrLiveTestSupport;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class AmazonProvisionrLiveTest extends ProvisionrLiveTestSupport {

    public static final Logger LOG = LoggerFactory.getLogger(AmazonProvisionrLiveTest.class);

    public static final int TEST_POOL_SIZE = 2;

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
    public void startProvisioningProcessForOnDemandInstances() throws Exception {
        startProvisioningProcess(null);
    }

    @Test
    public void startProvisioningProcessForSpotInstances() throws Exception {
        startProvisioningProcess("0.04");
    }

    private void startProvisioningProcess(String spotBid) throws Exception {
        waitForProcessDeployment(AmazonProvisionr.MANAGEMENT_PROCESS_KEY);

        final Provisionr provisionr = getOsgiService(Provisionr.class, 5000);

        Provider provider = collectProviderCredentialsFromSystemProperties()
            .option(ProviderOptions.REGION, getProviderProperty(
                ProviderOptions.REGION, ProviderOptions.DEFAULT_REGION))
            .createProvider();

        if (spotBid != null) {
            provider = provider.toBuilder()
                    .option(ProviderOptions.SPOT_BID, spotBid)
                    .createProvider();
        }

        final Network network = Network.builder().addRules(
            Rule.builder().anySource().icmp().createRule(),
            Rule.builder().anySource().port(22).protocol(Protocol.TCP).createRule()
        ).createNetwork();

        final Hardware hardware = Hardware.builder().type("t1.micro").createHardware();

        final AdminAccess adminAccess = AdminAccess.builder().asCurrentUser().createAdminAccess();

        final String destinationPath = "/home/" + adminAccess.getUsername() + "/axemblr.html";
        final Software software = Software.builder()
            .baseOperatingSystem("ubuntu-12.04")
            .file("http://axemblr.com", destinationPath)
            .createSoftware();

        JenkinsTemplate jenkins = new JenkinsTemplate();
        final Pool pool = jenkins.apply(Pool.builder()
            .provider(provider)
            .network(network)
            .adminAccess(adminAccess)
            .software(software)
            .hardware(hardware)
            .minSize(TEST_POOL_SIZE)
            .expectedSize(TEST_POOL_SIZE)
            .createPool());

        final String businessKey = "j-" + UUID.randomUUID().toString();
        String processInstanceId = provisionr.startPoolManagementProcess(businessKey, pool);

        try {
            waitForPoolStatus(provisionr, businessKey, PoolStatus.READY);

            List<Machine> machines = provisionr.getMachines(businessKey);
            assertTrue(machines.size() >= TEST_POOL_SIZE && machines.size() <= TEST_POOL_SIZE);

            for (Machine machine : machines) {
                assertSshCommand(machine, adminAccess, "test -f " + destinationPath);

                /* These are added through the Jenkins Debian template */
                assertSshCommand(machine, adminAccess, "hash git >/dev/null 2>&1");
                assertSshCommand(machine, adminAccess, "hash java >/dev/null 2>&1");
                assertSshCommand(machine, adminAccess, "test -f /etc/apt/sources.list.d/jenkins.list");
            }
        } finally {
            provisionr.destroyPool(businessKey);

            waitForPoolStatus(provisionr, businessKey, PoolStatus.TERMINATED);
            waitForProcessEnd(processInstanceId);
        }
    }

    private void assertSshCommand(Machine machine, AdminAccess adminAccess, String bashCommand) throws IOException {
        LOG.info("Checking return code for command '{}' on machine {}", bashCommand, machine.getExternalId());
        SSHClient client = Ssh.newClient(machine, adminAccess);
        try {
            Session session = client.startSession();
            try {
                session.allocateDefaultPTY();
                Session.Command command = session.exec(bashCommand);

                command.join();
                assertTrue("Exit code was " + command.getExitStatus() + " for command " + bashCommand,
                    command.getExitStatus() == 0);
            } finally {
                session.close();
            }
        } finally {
            client.close();
        }
    }

    private void waitForPoolStatus(Provisionr provisionr, String businessKey,
                                   String expectedStatus) throws InterruptedException, TimeoutException {
        for (int i = 0; i < 120; i++) {
            String status;
            try {
                status = provisionr.getStatus(businessKey);

            } catch (NoSuchElementException e) {
                LOG.info(String.format("Pool management process not found with key %s. " +
                    "Assuming process terminated as expected.", businessKey));
                return;  /* The process ended as expected */
            }

            if (status.equals(expectedStatus)) {
                LOG.info("Pool status is '{}'. Advancing.", status);
                return;
            } else {
                LOG.info("Pool status is '{}'. Waiting 10s for '{}'. Try {}/120",
                    new Object[]{status, expectedStatus, i});
                TimeUnit.SECONDS.sleep(10);
            }
        }
        throw new TimeoutException("Status check timed out after 20 minutes");
    }
}

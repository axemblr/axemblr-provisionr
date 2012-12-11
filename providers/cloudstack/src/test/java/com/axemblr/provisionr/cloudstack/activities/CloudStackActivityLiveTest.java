package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.cloudstack.CloudStackProvisionr;
import com.axemblr.provisionr.cloudstack.core.SecurityGroups;
import com.axemblr.provisionr.test.Generics;
import com.axemblr.provisionr.test.ProvisionrLiveTestSupport;
import com.google.common.base.Throwables;
import java.util.Set;
import java.util.UUID;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.rest.RestContext;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper base class for CloudStack Live tests.
 */
public class CloudStackActivityLiveTest<T extends CloudStackActivity> extends ProvisionrLiveTestSupport {

    public CloudStackActivityLiveTest() {
        super(CloudStackProvisionr.ID);
    }

    private static final Logger LOG = LoggerFactory.getLogger(DeleteSecurityGroupLiveTest.class);

    protected final String BUSINESS_KEY = "j-" + UUID.randomUUID().toString();
    /**
     * Cloud provider credentials collected from system properties.
     */
    protected Provider provider;
    /**
     * Instance of CloudStackActivity being tested. Automatically created from the
     * generic type class argument.
     */
    protected CloudStackActivity activity;

    protected RestContext<CloudStackClient, CloudStackAsyncClient> context;

    public CloudStackActivityLiveTest(String provisionrId) {
        super(provisionrId);
    }

    protected T createCloudStackActivitiInstance() {
        try {
            return getCloudStackActivityClass().newInstance();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    protected Class<T> getCloudStackActivityClass() {
        return Generics.getTypeParameter(getClass(), CloudStackActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        provider = collectProviderCredentialsFromSystemProperties().createProvider();
        LOG.info("Using provider {}", provider);

        activity = createCloudStackActivitiInstance();
        context = activity.newCloudStackClient(provider);
    }


    @After
    public void tearDown() throws Exception {
        context.close();
    }

    protected void logSecurityGroupDetails() {
        Set<SecurityGroup> securityGroups = SecurityGroups.getAll(context.getApi());
        LOG.info("Security Group count is {}", securityGroups.size());
        for (SecurityGroup securityGroup : securityGroups) {
            LOG.info("\tSecurity Group {}", securityGroup.getName());
        }
    }

    protected void logKeyPairs() {
        Set<SshKeyPair> keys = context.getApi().getSSHKeyPairClient().listSSHKeyPairs();
        LOG.info("Access Key count is {}", keys.size());
        for (SshKeyPair keyPair : keys) {
            LOG.info("\tKey {}", keyPair.getName());
        }
    }

    protected void logVirtualMachines() {
        Set<VirtualMachine> vms = context.getApi().getVirtualMachineClient().listVirtualMachines();
        LOG.info("Virtual machines count is {}", vms.size());
        for (VirtualMachine vm : vms) {
            LOG.info("\tVirtual machine {}", vm.toString());
        }
    }
}

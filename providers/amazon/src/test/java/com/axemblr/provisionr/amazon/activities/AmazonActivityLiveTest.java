package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.axemblr.provisionr.amazon.AmazonProvisionr;
import com.axemblr.provisionr.amazon.ProviderOptions;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.test.Generics;
import com.axemblr.provisionr.test.ProvisionrLiveTestSupport;
import com.google.common.base.Throwables;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AmazonActivityLiveTest<T extends AmazonActivity> extends ProvisionrLiveTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(AmazonActivityLiveTest.class);

    protected final String BUSINESS_KEY = "j-" + UUID.randomUUID().toString();

    /**
     * Cloud provider credentials collected from system properties
     */
    protected Provider provider;

    /**
     * Instance of the AmazonActivity being tested. Automatically created
     * from the generic type class argument
     */
    protected AmazonActivity activity;

    /**
     * Amazon EC2 client
     */
    protected AmazonEC2 client;

    public AmazonActivityLiveTest() {
        super(AmazonProvisionr.ID);
    }

    /**
     * Create an instance using the default constructor for the class type argument
     */
    protected T createAmazonActivityInstance() {
        try {
            return getAmazonActivityClass().newInstance();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Retrieve the generic class type argument.
     */
    protected Class<T> getAmazonActivityClass() {
        return Generics.getTypeParameter(getClass(), AmazonActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        final String region = getProviderProperty(ProviderOptions.REGION, ProviderOptions.DEFAULT_REGION);
        provider = collectProviderCredentialsFromSystemProperties()
            .option(ProviderOptions.REGION, region).createProvider();
        LOG.info("Using provider {}", provider);

        activity = createAmazonActivityInstance();
        client = activity.newAmazonEc2Client(provider);
    }

    @After
    public void tearDown() throws Exception {
        client.shutdown();
    }

    protected void quietlyDeleteSecurityGroupIfExists(String groupName) {
        try {
            client.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupName(groupName));

        } catch (AmazonServiceException e) {
            if (!e.getErrorCode().equals("InvalidGroup.NotFound")) {
                throw Throwables.propagate(e);
            }
        }
    }
}


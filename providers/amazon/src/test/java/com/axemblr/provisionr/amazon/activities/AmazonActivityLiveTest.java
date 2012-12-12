package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.axemblr.provisionr.amazon.AmazonProvisionr;
import com.axemblr.provisionr.amazon.ProviderOptions;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.amazon.core.ProviderClientCacheSupplier;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.test.Generics;
import com.axemblr.provisionr.test.ProvisionrLiveTestSupport;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import java.lang.reflect.Constructor;
import java.util.UUID;
import org.activiti.engine.delegate.DelegateExecution;
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
    protected T activity;

    /**
     * Amazon EC2 client
     */
    protected AmazonEC2 client;

    /**
     * A cache of AmazonEC2 client instances by Provider
     */
    private ProviderClientCache clientCache;

    public AmazonActivityLiveTest() {
        super(AmazonProvisionr.ID);
    }

    /**
     * Create an instance using the default constructor for the class type argument
     *
     * @param klass
     * @param clientCache cache for AmazonEC2 client connections
     */
    @SuppressWarnings("unchecked")
    protected <A extends AmazonActivity> A createAmazonActivityInstance(
        Class<A> klass, ProviderClientCache clientCache
    ) {
        try {
            Constructor constructor = klass.getConstructor(ProviderClientCache.class);
            return (A) constructor.newInstance(clientCache);

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

        clientCache = (new ProviderClientCacheSupplier()).get();
        client = clientCache.getUnchecked(provider);

        activity = createAmazonActivityInstance(getAmazonActivityClass(), clientCache);
    }

    @After
    public void tearDown() throws Exception {
        client.shutdown();
    }

    protected void executeActivitiesInSequence(
        DelegateExecution execution, Class<? extends AmazonActivity>... classes
    ) throws Exception {
        for (Class<? extends AmazonActivity> klass : classes) {
            createAmazonActivityInstance(klass, clientCache).execute(execution);
        }
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


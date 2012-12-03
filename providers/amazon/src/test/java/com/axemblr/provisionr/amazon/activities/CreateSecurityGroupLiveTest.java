package com.axemblr.provisionr.amazon.activities;

import com.axemblr.provisionr.amazon.AmazonProvisionr;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.test.ProvisionrLiveTestSupport;
import org.activiti.engine.delegate.JavaDelegate;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSecurityGroupLiveTest extends ProvisionrLiveTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(CreateSecurityGroupLiveTest.class);

    private final JavaDelegate activity = new CreateSecurityGroup();
    private Provider provider;

    public CreateSecurityGroupLiveTest() {
        super(AmazonProvisionr.ID);
    }

    @Before
    public void setUp() {
        provider = collectProviderCredentialsFromSystemProperties()
            .option("region", getProviderProperty("region", "us-east-1"))
            .createProvider();

        LOG.info("Using provider {}", provider);
    }

    @Test
    public void testCreateSecurityGroup() {

    }
}

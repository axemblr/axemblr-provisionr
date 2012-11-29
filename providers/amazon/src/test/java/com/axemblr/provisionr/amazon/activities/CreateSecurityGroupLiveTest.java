package com.axemblr.provisionr.amazon.activities;

import com.axemblr.provisionr.api.provider.Provider;
import org.activiti.engine.delegate.JavaDelegate;
import org.junit.Before;
import org.junit.Test;

public class CreateSecurityGroupLiveTest {

    private final JavaDelegate activity = new CreateSecurityGroup();

    private Provider provider;

    @Before
    public void setUp() {
        // provider = loadProviderWithOptionsOrSkipTests(AmazonProvisionr.ID, "region");
    }

    @Test
    public void testCreateSecurityGroup() {

    }
}

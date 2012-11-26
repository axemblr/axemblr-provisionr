package com.axemblr.provisionr;

import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.provider.ProviderBuilder;
import org.junit.Assume;

public class ProvisionrLiveTest {

    public Provider loadProviderWithOptionsOrSkipTests(String id, String... requiredOptions) {
        ProviderBuilder builder = Provider.builder()
            .id(id)
            .accessKey(getOrSkipTest(id, "accessKey"))
            .secretKey(getOrSkipTest(id, "secretKey"))
            .endpoint(getOrDefault(id, "endpoint", ""));

        for (String option : requiredOptions) {
            builder.option(option, getOrSkipTest(id, option));
        }
        return builder.createProvider();
    }

    private String getOrSkipTest(String id, String property) {
        String value = System.getProperty(String.format("test.%s.provider.%s", id, property));
        Assume.assumeNotNull(value);
        return value;
    }

    private String getOrDefault(String id, String property, String defaultValue) {
        return System.getProperty(String.format("test.%s.provider.%s", id, property), defaultValue);
    }
}

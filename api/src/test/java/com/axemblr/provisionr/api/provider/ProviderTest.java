package com.axemblr.provisionr.api.provider;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class ProviderTest {

    @Test
    public void testSerialization() {
        Provider provider = Provider.builder()
            .id("aws-ec2").accessKey("access").secretKey("secret")
            .option("location", "eu-west-1").createProvider();

        assertThat(provider.getAccessKey()).isEqualTo("access");
        assertThat(provider.getOptions()).containsKey("location");

        assertSerializable(provider, Provider.class);
    }

}

package com.axemblr.provisionr.api;

import com.axemblr.provisionr.api.provider.Provider;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class ProviderTest {

    @Test
    public void testCreateProvider() {
        Provider provider = Provider.builder()
            .id("aws-ec2").accessKey("access").secretKey("secret")
            .option("location", "eu-west-1").createProvider();

        assertThat(provider.getAccessKey()).isEqualTo("access");
        assertThat(provider.getOptions()).containsKey("location");
    }

}

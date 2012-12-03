package com.axemblr.provisionr.api.pool;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.api.provider.Provider;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class PoolTest {

    @Test
    public void testSerialization() {
        final Provider provider = Provider.builder()
            .id("aws-ec2").accessKey("access").secretKey("secret").createProvider();

        final Network network = Network.builder().addRules(
            Rule.builder().anySource().port(22).createRule(),
            Rule.builder().anySource().port(8088).createRule()
        ).createNetwork();

        final Software software = Software.builder()
            .packages("hadoop-0.20", "hadoop-0.20-native").createSoftware();

        Pool pool = Pool.builder()
            .provider(provider)
            .network(network)
            .software(software)
            .hardware(Hardware.builder().type("large").createHardware())
            .minSize(20)
            .expectedSize(25)
            .bootstrapTimeInSeconds(60 * 15)
            .cacheBaseImage(true)
            .createPool();

        assertThat(pool.getSoftware().getPackages()).contains("hadoop-0.20");

        assertSerializable(pool, Pool.class);
    }
}

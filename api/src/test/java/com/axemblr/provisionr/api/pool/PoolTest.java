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

package com.axemblr.provisionr.api.pool;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import static org.fest.assertions.api.Assertions.assertThat;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.software.Software;

import org.junit.Test;

public class PoolTest {

    @Test
    public void testSerialization() {
        final Provider provider = Provider.builder()
            .id("amazon").accessKey("access").secretKey("secret")
            .createProvider();

        final Network network = Network.builder().addRules(
            Rule.builder().anySource().port(22).tcp().createRule(),
            Rule.builder().anySource().port(8088).tcp().createRule()
        ).createNetwork();

        final AdminAccess adminAccess = AdminAccess.builder().username("admin").publicKey("ssh-rsa AAAAB3N")
            .privateKey("-----BEGIN RSA PRIVATE KEY-----\n").createAdminAccess();

        final Software software = Software.builder()
            .packages("hadoop-0.20", "hadoop-0.20-native").createSoftware();

        Pool pool = Pool.builder()
            .provider(provider)
            .network(network)
            .adminAccess(adminAccess)
            .software(software)
            .hardware(Hardware.builder().type("large").createHardware())
            .minSize(20)
            .expectedSize(25)
            .bootstrapTimeInSeconds(60 * 15)
            .createPool();


        assertThat(pool.getSoftware().getPackages()).contains("hadoop-0.20");
        assertThat(pool.toBuilder().createPool()).isEqualTo(pool);

        assertSerializable(pool, Pool.class);
    }
}

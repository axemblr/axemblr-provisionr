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

package com.axemblr.provisionr.amazon.config;

import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Optional;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class DefaultProviderConfigTest {

    @Test
    public void testProviderIsAbsentIfCredentialsAreEmpty() {
        DefaultProviderConfig config = new DefaultProviderConfig("", "", "us-east-1", "");
        assertThat(config.createProvider().isPresent()).isFalse();
    }

    @Test
    public void testProviderWithNoRegionAndEndpoint() {
        DefaultProviderConfig config = new DefaultProviderConfig("access", "secret", "", "");

        Optional<Provider> provider = config.createProvider();
        assertThat(provider.isPresent()).isTrue();

        assertThat(provider.get().getEndpoint().isPresent()).isFalse();
        assertThat(provider.get().getOptions().containsKey("region")).isFalse();
    }
}

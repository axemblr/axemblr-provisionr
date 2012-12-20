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

package com.axemblr.provisionr.cloudstack;

import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultProviderConfig {

    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String zoneId;
    private final String templateId;
    private final String serviceOffering;

    public DefaultProviderConfig(
        String endpoint, String accessKey, String secretKey, String zoneId, String templateId, String serviceOffering
    ) {
        this.endpoint = checkNotNull(endpoint, "endpoint is null");
        this.accessKey = checkNotNull(accessKey, "access key is null");
        this.secretKey = checkNotNull(secretKey, "secret key is null");
        this.zoneId = checkNotNull(zoneId, "zone id is null");
        this.templateId = checkNotNull(templateId, "template id is null");
        this.serviceOffering = checkNotNull(serviceOffering, "service offering is null");
    }

    public Optional<Provider> createProvider() {
        if (accessKey.isEmpty() || secretKey.isEmpty() || endpoint.isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(Provider.builder()
            .id(CloudStackProvisionr.ID)
            .endpoint(endpoint)
            .accessKey(accessKey)
            .secretKey(secretKey)
            .option(ProviderOptions.ZONE_ID, zoneId)
            .option(ProviderOptions.TEMPLATE_ID, templateId)
            .option(ProviderOptions.SERVICE_OFFERING, serviceOffering)
            .createProvider());
    }
}

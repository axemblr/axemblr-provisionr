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

    // TODO: move options up in the dependency hierarchy
    private static final String CLOUDSTACK_ZONE_ID_OPTION = "cloudstack.provider.zoneId";
    private static final String CLOUDSTACK_TEMPLATE_ID_OPTION = "cloudstack.provider.templateId";
    private static final String CLOUDSTACK_SERVICE_OFFERING_OPTION = "cloudstack.provider.serviceOffering";

    private final String endPoint;
    private final String accessKey;
    private final String secretKey;
    private final String zoneId;
    private final String templateId;
    private final String serviceOffering;

    public DefaultProviderConfig(
        String endPoint, String accessKey, String secretKey, String zoneId, String templateId, String serviceOffering
    ) {
        this.endPoint = checkNotNull(endPoint);
        this.accessKey = checkNotNull(accessKey);
        this.secretKey = checkNotNull(secretKey);
        this.zoneId = checkNotNull(zoneId);
        this.templateId = checkNotNull(templateId);
        this.serviceOffering = checkNotNull(serviceOffering);
    }

    public Optional<Provider> createProvider() {
        if (accessKey.isEmpty() || secretKey.isEmpty() || endPoint.isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(Provider.builder()
            .id(CloudStackProvisionr.ID)
            .endpoint(endPoint)
            .accessKey(accessKey)
            .secretKey(secretKey)
            .option(CLOUDSTACK_ZONE_ID_OPTION, zoneId)
            .option(CLOUDSTACK_TEMPLATE_ID_OPTION, templateId)
            .option(CLOUDSTACK_SERVICE_OFFERING_OPTION, serviceOffering)
            .createProvider());
    }
}

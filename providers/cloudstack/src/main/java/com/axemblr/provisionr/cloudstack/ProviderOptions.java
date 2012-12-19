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

public class ProviderOptions {

    private ProviderOptions() {
        throw new RuntimeException(ProviderOptions.class.getName() + " should not be instantiated");
    }

    public static final String CLOUDSTACK_ZONE_ID_OPTION = "zoneId";
    public static final String CLOUDSTACK_TEMPLATE_ID_OPTION = "templateId";
    public static final String CLOUDSTACK_SERVICE_OFFERING_OPTION = "serviceOffering";
}

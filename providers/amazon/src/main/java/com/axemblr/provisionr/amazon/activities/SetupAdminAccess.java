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

package com.axemblr.provisionr.amazon.activities;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.Mustache;
import com.axemblr.provisionr.core.activities.PuppetActivity;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.Map;

public class SetupAdminAccess extends PuppetActivity {

    public static final String DEFAULT_UBUNTU_AMI_USER = "ubuntu";

    public static final String ADMIN_ACCESS_TEMPLATE = "/com/axemblr/provisionr/amazon/puppet/adminaccess.pp.mustache";
    public static final String SSHD_CONFIG_TEMPLATE = "/com/axemblr/provisionr/amazon/puppet/sshd_config.mustache";
    public static final String SUDOERS_TEMPLATE = "/com/axemblr/provisionr/amazon/puppet/sudoers";

    public SetupAdminAccess() {
        super("adminaccess");
    }

    @Override
    public AdminAccess overrideAdminAccess(Pool pool) {
        return pool.getAdminAccess().toBuilder().username(DEFAULT_UBUNTU_AMI_USER).createAdminAccess();
    }

    @Override
    public String createPuppetScript(Pool pool, Machine machine) throws Exception {
        return Mustache.toString(getClass(), ADMIN_ACCESS_TEMPLATE,
            ImmutableMap.of(
                "user", pool.getAdminAccess().getUsername(),
                "publicKey", getRawSshKey(pool))
        );
    }

    private String getRawSshKey(Pool pool) {
        return pool.getAdminAccess().getPublicKey().split(" ")[1];
    }

    @Override
    public Map<String, String> createAdditionalFiles(Pool pool, Machine machine) throws IOException {
        return ImmutableMap.of(
            "/tmp/sshd_config",
            Mustache.toString(getClass(), SSHD_CONFIG_TEMPLATE,
                ImmutableMap.of("user", pool.getAdminAccess().getUsername())),
            "/tmp/sudoers",
            Resources.toString(Resources.getResource(getClass(), SUDOERS_TEMPLATE), Charsets.UTF_8)
        );

    }
}

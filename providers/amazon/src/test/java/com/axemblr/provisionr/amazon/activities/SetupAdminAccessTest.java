/*
 * Copyright (c) 2013 S.C. Axemblr Software Solutions S.R.L
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
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.activities.PuppetActivity;
import com.axemblr.provisionr.test.Constants;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SetupAdminAccessTest {

    @Test
    public void testCreatePuppetScript() throws Exception {
        Pool pool = mock(Pool.class);

        final AdminAccess adminAccess = AdminAccess.builder()
            .privateKey(Constants.PRIVATE_KEY)
            .publicKey(Constants.PUBLIC_KEY)
            .username(System.getProperty("user.name"))
            .createAdminAccess();

        when(pool.getAdminAccess()).thenReturn(adminAccess);

        PuppetActivity activity = new SetupAdminAccess();
        String content = activity.createPuppetScript(pool, null);

        final String username = adminAccess.getUsername();

        assertThat(content).contains(username)
            .contains(adminAccess.getPublicKey().split(" ")[1])
            .contains(String.format("user { \"%s\":", username))
            .contains(String.format("file { \"/home/%s/.ssh\":", username));
    }
}

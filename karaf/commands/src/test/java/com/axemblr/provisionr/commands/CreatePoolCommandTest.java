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

package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.core.templates.JenkinsTemplate;
import com.axemblr.provisionr.core.templates.PoolTemplate;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.felix.service.command.CommandSession;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreatePoolCommandTest {

    public static final String TEST_PROVISIONR_ID = "amazon";
    public static final String TEST_BUSINESS_KEY = "j-123";

    @Test
    public void testCreatePoolStartsTheManagementProcess() throws Exception {
        final Provisionr service = newProvisionrMockWithId(TEST_PROVISIONR_ID);
        final Pool pool = mock(Pool.class);

        final List<Provisionr> services = ImmutableList.of(service);
        final List<PoolTemplate> templates = ImmutableList.<PoolTemplate>of(new JenkinsTemplate());
        CreatePoolCommand command = new CreatePoolCommand(services, templates) {
            @Override
            protected Pool createPoolFromArgumentsAndServiceDefaults(Provisionr service) {
                return pool;
            }
        };
        command.setId(TEST_PROVISIONR_ID);
        command.setKey(TEST_BUSINESS_KEY);

        CommandSession session = mock(CommandSession.class);
        String output = (String) command.execute(session);

        verify(service).startPoolManagementProcess(TEST_BUSINESS_KEY, pool);
        assertThat(output).isEqualTo("Pool management process started (id: null)");
    }

    @Test(expected = NoSuchElementException.class)
    public void testProvisioningServiceNotFound() throws Exception {
        CreatePoolCommand command = new CreatePoolCommand(Collections.<Provisionr>emptyList(),
            Collections.<PoolTemplate>emptyList());
        command.setId("dummy");

        CommandSession session = mock(CommandSession.class);
        command.execute(session);
    }

    @Test
    public void testCreatePoolWithTemplate() {
        final JenkinsTemplate template = new JenkinsTemplate();
        CreatePoolCommand command = new CreatePoolCommand(Collections.<Provisionr>emptyList(),
            ImmutableList.<PoolTemplate>of(template)) {

            @Override
            protected AdminAccess collectCurrentUserCredentialsForAdminAccess() {
                return mock(AdminAccess.class);
            }
        };

        command.setId("service");
        command.setKey("key");
        command.setTemplate(template.getId());

        Provisionr service = mock(Provisionr.class);
        when(service.getDefaultProvider()).thenReturn(Optional.of(mock(Provider.class)));

        Pool pool = command.createPoolFromArgumentsAndServiceDefaults(service);

        assertThat(pool.getSoftware().getRepositories()).hasSize(1);
        assertThat(pool.getSoftware().getPackages()).contains("jenkins").contains("git-core");
    }

    private Provisionr newProvisionrMockWithId(String id) {
        Provisionr service = mock(Provisionr.class);
        when(service.getId()).thenReturn(id);
        return service;
    }
}

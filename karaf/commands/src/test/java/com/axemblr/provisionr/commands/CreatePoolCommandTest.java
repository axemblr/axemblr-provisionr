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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.provider.ProviderBuilder;
import com.axemblr.provisionr.core.templates.PoolTemplate;
import com.axemblr.provisionr.core.templates.xml.XmlTemplate;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import java.io.IOException;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.felix.service.command.CommandSession;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class CreatePoolCommandTest {

    public static final String TEST_PROVISIONR_ID = "amazon";
    public static final String TEST_BUSINESS_KEY = "j-123";

    @Test
    public void testCreatePoolStartsTheManagementProcess() throws Exception {
        final Provisionr service = newProvisionrMockWithId(TEST_PROVISIONR_ID);
        final Pool pool = mock(Pool.class);

        final List<Provisionr> services = ImmutableList.of(service);
        final List<PoolTemplate> templates = ImmutableList.of();
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
        final PoolTemplate template = XmlTemplate.newXmlTemplate(readDefaultTemplate("jenkins"));

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
        Provider provider = newProviderMockWithBuilder();
        when(service.getDefaultProvider()).thenReturn(Optional.of(provider));

        Pool pool = command.createPoolFromArgumentsAndServiceDefaults(service);

        assertThat(pool.getSoftware().getRepositories()).hasSize(1);
        assertThat(pool.getSoftware().getPackages()).contains("jenkins").contains("git-core");
    }

    @Test
    public void testProviderSpecificOptions() {
        CreatePoolCommand command = new CreatePoolCommand(Collections.<Provisionr>emptyList(),
            ImmutableList.<PoolTemplate>of()) {

            @Override
            protected AdminAccess collectCurrentUserCredentialsForAdminAccess() {
                return mock(AdminAccess.class);
            }
        };
        command.setId("service");
        command.setKey("key");
        command.setProviderOptions(Lists.newArrayList("spotBid=0.07"));

        Provisionr service = mock(Provisionr.class);
        Provider provider = newProviderMockWithBuilder();
        when(service.getDefaultProvider()).thenReturn(Optional.of(provider));

        command.createPoolFromArgumentsAndServiceDefaults(service);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> argument = (ArgumentCaptor<Map<String, String>>) (Object) 
                ArgumentCaptor.forClass(Map.class);
        verify(provider.toBuilder()).options(argument.capture());

        assertThat(argument.getValue().containsKey("spotBid")).isTrue();
        assertThat(argument.getValue().get("spotBid")).isEqualTo("0.07");
    }

    private Provisionr newProvisionrMockWithId(String id) {
        Provisionr service = mock(Provisionr.class);
        when(service.getId()).thenReturn(id);
        return service;
    }

    private Provider newProviderMockWithBuilder() {
        Provider provider = mock(Provider.class);
        ProviderBuilder providerBuilder = mock(ProviderBuilder.class);
        when(providerBuilder.options(anyMapOf(String.class, String.class))).thenReturn(providerBuilder);
        when(providerBuilder.createProvider()).thenReturn(provider);
        when(provider.toBuilder()).thenReturn(providerBuilder);
        return provider;
    }


    private String readDefaultTemplate(String name) {
        try {
            return Resources.toString(Resources.getResource(PoolTemplate.class,
                String.format("/com/axemblr/provisionr/core/templates/%s.xml", name)), Charsets.UTF_8);

        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}

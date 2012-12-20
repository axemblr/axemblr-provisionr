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
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
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

        CreatePoolCommand command = new CreatePoolCommand(ImmutableList.of(service)) {
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
        CreatePoolCommand command = new CreatePoolCommand(Collections.<Provisionr>emptyList());
        command.setId("dummy");

        CommandSession session = mock(CommandSession.class);
        command.execute(session);
    }

    private Provisionr newProvisionrMockWithId(String id) {
        Provisionr service = mock(Provisionr.class);
        when(service.getId()).thenReturn(id);
        return service;
    }
}

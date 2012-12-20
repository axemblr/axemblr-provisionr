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
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.apache.felix.service.command.CommandSession;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListServicesCommandTest {

    @Test
    public void testListServices() throws Exception {
        List<Provisionr> services = ImmutableList.of(
            newProvisionrMockWithId("p1"),
            newProvisionrMockWithId("p2")
        );

        ListServicesCommand command = new ListServicesCommand(services);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream);
        command.setOut(out);

        CommandSession session = mock(CommandSession.class);
        command.execute(session);
        out.close();

        for (Provisionr service : services) {
            verify(service).getId();
        }

        assertThat(outputStream.toString()).isEqualTo("Services: p1, p2\n");
    }

    private Provisionr newProvisionrMockWithId(String id) {
        Provisionr service = mock(Provisionr.class);
        when(service.getId()).thenReturn(id);
        return service;
    }
}

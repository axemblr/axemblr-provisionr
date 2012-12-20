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
import java.util.NoSuchElementException;
import org.apache.felix.service.command.CommandSession;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DestroyPoolCommandTest {

    public static final String TEST_PROVISIONR_ID = "amazon";

    public static final String TEST_BUSINESS_KEY = "dummy";

    @Test
    public void testDestroyPool() throws Exception {
        Provisionr service = mock(Provisionr.class);
        when(service.getId()).thenReturn(TEST_PROVISIONR_ID);

        DestroyPoolCommand command = new DestroyPoolCommand(ImmutableList.of(service));
        command.setId(TEST_PROVISIONR_ID);
        command.setBusinessKey(TEST_BUSINESS_KEY);

        CommandSession session = mock(CommandSession.class);
        command.execute(session);

        verify(service).destroyPool(TEST_BUSINESS_KEY);
    }

    @Test(expected = NoSuchElementException.class)
    public void testFailsWithAnEmptyServiceList() throws Exception {
        DestroyPoolCommand command = new DestroyPoolCommand(ImmutableList.<Provisionr>of());
        command.setId(TEST_PROVISIONR_ID);

        CommandSession session = mock(CommandSession.class);
        command.execute(session);
    }

}

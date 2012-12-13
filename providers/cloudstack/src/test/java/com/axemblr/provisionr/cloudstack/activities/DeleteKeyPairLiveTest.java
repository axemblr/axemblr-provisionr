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

package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.core.KeyPairs;
import com.axemblr.provisionr.cloudstack.ProcessVariables;
import com.axemblr.provisionr.core.CoreProcessVariables;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DeleteKeyPairLiveTest extends CloudStackActivityLiveTest<DeleteKeyPair> {

    private final String KEYPAIR_NAME = KeyPairs.formatNameFromBusinessKey(BUSINESS_KEY);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        logKeyPairs();
        context.getApi().getSSHKeyPairClient().registerSSHKeyPair(KEYPAIR_NAME, getResourceAsString("keys/test.pub"));
    }

    @Override
    @After
    public void tearDown() throws Exception {
        context.getApi().getSSHKeyPairClient().deleteSSHKeyPair(KEYPAIR_NAME);
        logKeyPairs();
        super.tearDown();
    }

    @Test
    public void testDeleteKeyPair() throws Exception {
        final AdminAccess adminAccess = AdminAccess.builder()
            .username("admin")
            .publicKey(getResourceAsString("keys/test.pub"))
            .privateKey(getResourceAsString("keys/test"))
            .createAdminAccess();

        DelegateExecution execution = mock(DelegateExecution.class);
        Pool pool = mock(Pool.class);

        when(pool.getAdminAccess()).thenReturn(adminAccess);
        when(pool.getProvider()).thenReturn(provider);

        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);
        when(execution.getVariable(CoreProcessVariables.POOL)).thenReturn(pool);

        activity.execute(execution);
        assertKeyNotFound(KEYPAIR_NAME);
        /* the second call should just do nothing */
        activity.execute(execution);
    }

    private void assertKeyNotFound(String keyName) {
        assertThat(context.getApi().getSSHKeyPairClient().getSSHKeyPair(keyName)).isNull();
    }
}

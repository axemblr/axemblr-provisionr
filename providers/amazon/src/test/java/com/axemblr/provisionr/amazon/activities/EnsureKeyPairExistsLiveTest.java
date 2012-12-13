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

import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.KeyPairs;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnsureKeyPairExistsLiveTest extends AmazonActivityLiveTest<EnsureKeyPairExists> {

    /**
     * Computed in an Amazon specific way
     */
    public static final String TEST_KEY_FINGERPRINT = "2f:e9:a0:bc:17:71:3a:7e:d7:c0:16:99:0d:62:8e:be";

    private final String KEYPAIR_NAME = KeyPairs.formatNameFromBusinessKey(BUSINESS_KEY);

    @Override
    public void tearDown() throws Exception {
        client.deleteKeyPair(new DeleteKeyPairRequest().withKeyName(KEYPAIR_NAME));
        super.tearDown();
    }

    @Test
    public void testEnsureKeyPairExists() throws Exception {
        final AdminAccess adminAccess = AdminAccess.builder()
            .username("admin")
            .publicKey(getResourceAsString("keys/test.pub"))
            .privateKey(getResourceAsString("keys/test"))
            .createAdminAccess();

        DelegateExecution execution = mock(DelegateExecution.class);
        Pool pool = mock(Pool.class);

        when(pool.getProvider()).thenReturn(provider);
        when(pool.getAdminAccess()).thenReturn(adminAccess);

        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);
        when(execution.getVariable(CoreProcessVariables.POOL)).thenReturn(pool);

        activity.execute(execution);
        assertKeyPairWasImportedAsExpected();

        /* the second call should just re-import the key */
        activity.execute(execution);
        assertKeyPairWasImportedAsExpected();
    }

    private void assertKeyPairWasImportedAsExpected() {
        final DescribeKeyPairsRequest request = new DescribeKeyPairsRequest().withKeyNames(KEYPAIR_NAME);
        DescribeKeyPairsResult result = client.describeKeyPairs(request);

        assertThat(result.getKeyPairs()).hasSize(1);
        assertThat(result.getKeyPairs().get(0).getKeyFingerprint()).isEqualTo(TEST_KEY_FINGERPRINT);
    }
}

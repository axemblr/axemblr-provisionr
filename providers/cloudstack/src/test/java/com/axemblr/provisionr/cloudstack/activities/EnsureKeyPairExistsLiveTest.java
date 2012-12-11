package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.core.KeyPairs;
import com.axemblr.provisionr.cloudstack.ProcessVariables;
import java.io.IOException;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnsureKeyPairExistsLiveTest extends CloudStackActivityLiveTest<EnsureKeyPairExists> {

    public static final String TEST_KEY_FINGERPRINT = "15:0b:a4:43:dd:58:19:9e:84:ca:db:31:a8:6b:b6:c3";
    private final String KEYPAIR_NAME = KeyPairs.formatNameFromBusinessKey(BUSINESS_KEY);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        logKeyPairs();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        context.getApi().getSSHKeyPairClient().deleteSSHKeyPair(KEYPAIR_NAME);
        logKeyPairs();
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
        when(execution.getVariable(ProcessVariables.POOL)).thenReturn(pool);

        activity.execute(execution);
        assertKeyPairWasImportedAsExpected();

        /* the second call should just re-import the key */
        activity.execute(execution);
        assertKeyPairWasImportedAsExpected();
    }

    private void assertKeyPairWasImportedAsExpected() throws IOException {
        SshKeyPair pair = context.getApi().getSSHKeyPairClient().getSSHKeyPair(KEYPAIR_NAME);
        assertThat(pair.getFingerprint()).isEqualTo(TEST_KEY_FINGERPRINT);
    }
}

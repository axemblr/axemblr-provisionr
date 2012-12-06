package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.axemblr.provisionr.amazon.KeyPairs;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnsureKeyPairExistsLiveTest extends AmazonActivityLiveTest<EnsureKeyPairExists> {

    public static final String TEST_KEY_FINGERPRINT = "2f:e9:a0:bc:17:71:3a:7e:d7:c0:16:99:0d:62:8e:be";

    private final String KEYPAIR_NAME = KeyPairs.formatNameFromBusinessKey(BUSINESS_KEY);

    @Override
    public void tearDown() {
        try {
            client.deleteKeyPair(new DeleteKeyPairRequest().withKeyName(KEYPAIR_NAME));
        } catch (Exception e) { /* ignore */ }
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
        when(execution.getVariable("pool")).thenReturn(pool);

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

    public String getResourceAsString(String resource) throws IOException {
        return Resources.toString(Resources.getResource(resource), Charsets.UTF_8);
    }
}

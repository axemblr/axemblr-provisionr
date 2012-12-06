package com.axemblr.provisionr.api.access;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class AdminAccessTest {

    @Test
    public void testSerialization() {
        AdminAccess adminAccess = AdminAccess.builder().username("admin").publicKey("ssh-rsa AAAAB3N")
            .privateKey("-----BEGIN RSA PRIVATE KEY-----\n").createAdminAccess();

        assertThat(adminAccess.getUsername()).isEqualTo("admin");
        assertThat(adminAccess.toBuilder().createAdminAccess()).isEqualTo(adminAccess);

        assertSerializable(adminAccess, AdminAccess.class);
    }
}

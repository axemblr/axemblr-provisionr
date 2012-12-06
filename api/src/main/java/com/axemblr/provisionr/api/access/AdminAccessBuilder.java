package com.axemblr.provisionr.api.access;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class AdminAccessBuilder {

    private String username;
    private String publicKey;
    private String privateKey;

    public AdminAccessBuilder username(String username) {
        this.username = checkNotNull(username, "username is null");
        return this;
    }

    public AdminAccessBuilder publicKey(String publicKey) {
        checkArgument(publicKey.startsWith("ssh-rsa "), "The key does not start with ssh-rsa as expected");
        this.publicKey = checkNotNull(publicKey, "publicKey is null");
        return this;
    }

    public AdminAccessBuilder privateKey(String privateKey) {
        checkArgument(privateKey.startsWith("-----BEGIN RSA PRIVATE KEY-----"),
            "The key does not start with -----BEGIN RSA PRIVATE KEY----- as expected");
        this.privateKey = checkNotNull(privateKey, "privateKey is null");
        return this;
    }

    public AdminAccess createAdminAccess() {
        return new AdminAccess(username, publicKey, privateKey);
    }
}
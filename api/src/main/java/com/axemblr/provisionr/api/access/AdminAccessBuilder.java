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

package com.axemblr.provisionr.api.access;

import com.google.common.base.Charsets;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import java.io.File;

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

    public AdminAccessBuilder asCurrentUser() {
        String userHome = System.getProperty("user.home");

        try {
            String publicKey = Files.toString(new File(userHome, ".ssh/id_rsa.pub"), Charsets.UTF_8);
            String privateKey = Files.toString(new File(userHome, ".ssh/id_rsa"), Charsets.UTF_8);

            return username(System.getProperty("user.name")).publicKey(publicKey).privateKey(privateKey);

        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public AdminAccess createAdminAccess() {
        return new AdminAccess(username, publicKey, privateKey);
    }
}
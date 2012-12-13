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

import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.Serializable;

/**
 * Describe the only user allowed to access machine from the pool over SSH
 * <p/>
 * This user will support only key-based SSH authentication have
 * password-less sudo access
 */
public class AdminAccess implements Serializable {

    public static AdminAccessBuilder builder() {
        return new AdminAccessBuilder();
    }

    private final String username;
    private final String publicKey;
    private final String privateKey;

    AdminAccess(String username, String publicKey, String privateKey) {
        this.username = checkNotNull(username, "username is null");
        this.publicKey = checkNotNull(publicKey, "publicKey is null");
        this.privateKey = checkNotNull(privateKey, "privateKey is null");
    }

    public String getUsername() {
        return username;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public AdminAccessBuilder toBuilder() {
        return builder().username(username)
            .publicKey(publicKey).privateKey(privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username, publicKey, privateKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AdminAccess other = (AdminAccess) obj;
        return Objects.equal(this.username, other.username)
            && Objects.equal(this.publicKey, other.publicKey)
            && Objects.equal(this.privateKey, other.privateKey);
    }

    @Override
    public String toString() {
        return "AdminAccess{" +
            "username='" + username + '\'' +
            ", publicKey='" + publicKey + '\'' +
            '}';
    }
}

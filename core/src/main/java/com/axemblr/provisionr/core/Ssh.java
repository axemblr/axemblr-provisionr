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

package com.axemblr.provisionr.core;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.pool.Machine;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.security.PublicKey;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ssh {

    private static final Logger LOG = LoggerFactory.getLogger(Ssh.class);

    private Ssh() {
    }

    /**
     * Accept any host key from the remote machines
     */
    private enum AcceptAnyHostKeyVerifier implements HostKeyVerifier {
        INSTANCE;

        @Override
        public boolean verify(String hostname, int port, PublicKey key) {
            String keyHash = Hashing.md5().hashBytes(key.getEncoded()).toString();
            LOG.info("Automatically accepting host key for {}:{} with md5 hash {}",
                new Object[]{hostname, port, keyHash});
            return true;
        }
    }

    /**
     * Create a new {@code SSHClient} connected to the remote machine using the
     * AdminAccess credentials as provided
     */
    public static SSHClient newClient(
        Machine machine, AdminAccess adminAccess, int timeoutInMillis
    ) throws IOException {
        checkArgument(timeoutInMillis >= 0, "timeoutInMillis should be positive or 0");

        final SSHClient client = new SSHClient();
        client.addHostKeyVerifier(AcceptAnyHostKeyVerifier.INSTANCE);

        if (timeoutInMillis != 0) {
            client.setConnectTimeout(timeoutInMillis);
            client.setTimeout(timeoutInMillis);
        }
        client.connect(machine.getPublicDnsName(), machine.getSshPort());

        OpenSSHKeyFile key = new OpenSSHKeyFile();
        key.init(adminAccess.getPrivateKey(), adminAccess.getPublicKey());
        client.authPublickey(adminAccess.getUsername(), key);

        return client;
    }
}

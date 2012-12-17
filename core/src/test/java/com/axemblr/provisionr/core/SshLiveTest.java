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
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class SshLiveTest {

    @Test
    public void testConnectToLocalhost() throws IOException {
        final Machine localhost = Machine.builder().externalId("localhost")
            .publicDnsName("localhost").publicIp("127.0.0.1")
            .privateDnsName("localhost").privateIp("127.0.0.1").createMachine();

        final AdminAccess adminAccess = AdminAccess.builder().asCurrentUser().createAdminAccess();

        SSHClient client = Ssh.newClient(localhost, adminAccess, 1000);
        try {
            Session session = client.startSession();
            try {
                final Session.Command command = session.exec("echo 'Testing SSH execution'");
                String output = CharStreams.toString(new InputStreamReader(command.getInputStream()));

                command.join();
                assertThat(output).contains("Testing SSH execution");

            } finally {
                session.close();
            }
        } finally {
            client.close();
        }
    }

}

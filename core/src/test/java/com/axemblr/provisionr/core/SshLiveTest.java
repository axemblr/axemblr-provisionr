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
import com.axemblr.provisionr.core.logging.StreamLogger;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class SshLiveTest {

    private static final Logger LOG = LoggerFactory.getLogger(SshLiveTest.class);

    private final Machine localhost = Machine.builder()
        .externalId("localhost")
        .publicDnsName("localhost").publicIp("127.0.0.1")
        .privateDnsName("localhost").privateIp("127.0.0.1")
        .createMachine();

    private final AdminAccess adminAccess = AdminAccess.builder()
        .asCurrentUser().createAdminAccess();


    @Test
    public void testConnectToLocalhostAndCollectOutput() throws IOException {
        SSHClient client = Ssh.newClient(localhost, adminAccess, 1000);
        try {
            Session session = client.startSession();
            try {
                final Session.Command command = session.exec("echo 'stdout' && echo 'stderr' 1>&2");

                String stdout = CharStreams.toString(new InputStreamReader(command.getInputStream()));
                String stderr = CharStreams.toString(new InputStreamReader(command.getErrorStream()));

                command.join();
                assertThat(command.getExitStatus()).isEqualTo(0);
                assertThat(command.getExitErrorMessage()).isNull();

                assertThat(stdout).contains("stdout");
                assertThat(stderr).contains("stderr");

            } finally {
                session.close();
            }
        } finally {
            client.close();
        }
    }

    @Test
    public void testConnectStreamLoggerToCommand() throws IOException, InterruptedException {
        SSHClient client = Ssh.newClient(localhost, adminAccess, 1000);
        try {
            Session session = client.startSession();
            try {
                final Session.Command command = session.exec("echo 'line1' && echo && echo 'line2'");
                final List<String> lines = Lists.newCopyOnWriteArrayList();

                StreamLogger logger = new StreamLogger(command.getInputStream(), LOG, MarkerFactory.getMarker("live")) {
                    @Override
                    protected void log(Logger logger, Marker marker, String line) {
                        logger.info(marker, line);  /* just for visual inspection */
                        lines.add(line);
                    }
                };
                logger.start();

                command.join();
                logger.join();

                assertThat(lines).hasSize(2).contains("line1", "line2");

            } finally {
                session.close();
            }
        } finally {
            client.close();
        }
    }
}

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

import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.axemblr.provisionr.core.Mustache;
import com.axemblr.provisionr.core.Ssh;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.xfer.InMemorySourceFile;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetupAdminAccess implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SetupAdminAccess.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Pool pool = (Pool) execution.getVariable(CoreProcessVariables.POOL);
        checkNotNull(pool, "Please add the pool description as a process " +
            "variable with the name '%s'.", CoreProcessVariables.POOL);

        Machine machine = (Machine) execution.getVariable("machine");
        LOG.info(">> Connecting to machine {}", machine);

        final String recipe = Mustache.toString(getClass(),
            "/com/axemblr/provisionr/amazon/puppet/adminaccess.pp.mustache",
            ImmutableMap.of(
                "user", pool.getAdminAccess().getUsername(),
                "publicKey", pool.getAdminAccess().getPublicKey().split(" ")[1])
        );

        SSHClient client = Ssh.newClient(machine,
            pool.getAdminAccess().toBuilder().username("ubuntu").createAdminAccess(),
            30000 /* milliseconds */);
        try {
            client.newSCPFileTransfer().upload(new InMemorySourceFile() {
                @Override
                public String getName() {
                    return "adminaccess.pp";
                }

                @Override
                public long getLength() {
                    return recipe.getBytes().length;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(recipe.getBytes());
                }
            }, "/tmp/adminaccess.pp");


            Session session = client.startSession();
            try {
                session.allocateDefaultPTY();
                final String shellCommand = "while ! which puppet &> /dev/null ; " +
                    "do echo 'Puppet command not found. Waiting for userdata.sh script to finish (10s)' " +
                    "&& sleep 10; " +
                    "done " +
                    "&& sudo puppet apply /tmp/adminaccess.pp";
                Session.Command command = session.exec(shellCommand);

                Ssh.logCommandOutput(LOG, machine.getExternalId(), command);
                command.join();

                if (command.getExitStatus() != 0) {
                    throw new RuntimeException(String.format("Failed to execute '%s'. Exit code: %d. Exit message: %s",
                        shellCommand, command.getExitStatus(), command.getExitErrorMessage()));

                } else {
                    LOG.info("<< Command completed successfully with exit code 0");
                }

            } finally {
                session.close();
            }
        } finally {
            client.close();
        }
    }
}

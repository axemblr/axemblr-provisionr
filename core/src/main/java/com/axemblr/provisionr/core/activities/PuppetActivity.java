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

package com.axemblr.provisionr.core.activities;

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.axemblr.provisionr.core.Ssh;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract activity useful for implementing activities that execute
 * puppet scripts on pool machines
 */
public abstract class PuppetActivity implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(PuppetActivity.class);

    /**
     * Puppet apply had no failures during the current transaction
     * <p/>
     * --detailed-exitcodes Provide transaction information via exit codes. If this is
     * enabled, an exit code of '2' means there were changes, an exit code of '4' means
     * there were failures during the transaction, and an exit code of '6' means there
     * were both changes and failures.
     *
     * @see <a href="http://docs.puppetlabs.com/man/apply.html" />
     */
    public static final int PUPPET_FINISHED_WITH_NO_FAILURES = 2;

    private final String remoteFileName;

    public PuppetActivity(String remoteFileName) {
        this.remoteFileName = checkNotNull(remoteFileName);
    }

    /**
     * This method creates a Puppet script for remote execution
     */
    public abstract String createPuppetScript(Pool pool, Machine machine) throws Exception;

    /**
     * Override this method to change the credentials used for SSH access
     */
    public AdminAccess overrideAdminAccess(Pool pool) {
        return pool.getAdminAccess();
    }

    /**
     * Map of additional files to create on the remote machine. Contains pairs of (remotePath, content)
     */
    public Map<String, String> createAdditionalFiles(Pool pool, Machine machine) throws Exception {
        return ImmutableMap.of();
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Pool pool = (Pool) execution.getVariable(CoreProcessVariables.POOL);
        checkNotNull(pool, "Please add the pool description as a process " +
            "variable with the name '%s'.", CoreProcessVariables.POOL);

        Machine machine = (Machine) execution.getVariable("machine");
        checkNotNull(machine, "expecting a process variable named 'machine'");

        LOG.info(">> Connecting to machine {} to run puppet script", machine);

        SSHClient client = Ssh.newClient(machine, overrideAdminAccess(pool));
        try {
            for (Map.Entry<String, String> entry : createAdditionalFiles(pool, machine).entrySet()) {
                Ssh.createFile(client, /* content = */ entry.getValue(), 0600, /* destination= */ entry.getKey());
            }

            final String destination = "/tmp/" + remoteFileName + ".pp";
            Ssh.createFile(client, createPuppetScript(pool, machine), 0600, destination);

            Session session = client.startSession();
            try {
                session.allocateDefaultPTY();

                // TODO: extract this loop outside of this activity (probably using a business process error)
                final String runScriptWithWaitCommand = "while ! which puppet &> /dev/null ; " +
                    "do echo 'Puppet command not found. Waiting for userdata.sh script to finish (10s)' " +
                    "&& sleep 10; " +
                    "done " +
                    "&& sudo puppet apply --detailed-exitcodes --debug --verbose " + destination;
                Session.Command command = session.exec(runScriptWithWaitCommand);

                Ssh.logCommandOutput(LOG, machine.getExternalId(), command);
                command.join();

                final Integer exitStatus = command.getExitStatus();
                if (exitStatus != PUPPET_FINISHED_WITH_NO_FAILURES && exitStatus != 0) {
                    throw new RuntimeException(String.format("Failed to execute puppet. " +
                        "Exit code: %d. Exit message: %s", exitStatus, command.getExitErrorMessage()));

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

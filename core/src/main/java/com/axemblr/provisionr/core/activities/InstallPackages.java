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

import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.axemblr.provisionr.core.Mustache;
import com.axemblr.provisionr.core.Ssh;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstallPackages implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(InstallPackages.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Pool pool = (Pool) execution.getVariable(CoreProcessVariables.POOL);
        checkNotNull(pool, "Please add the pool description as a process " +
            "variable with the name '%s'.", CoreProcessVariables.POOL);

        Machine machine = (Machine) execution.getVariable("machine");
        LOG.info(">> Connecting to machine {} to install packages", machine);

        SSHClient client = Ssh.newClient(machine, pool.getAdminAccess());
        try {
            String puppetScript = Mustache.toString(InstallPackages.class,
                "/com/axemblr/provisionr/core/puppet/packages.pp.mustache",
                ImmutableMap.of("packages", packagesAsListOfMaps(pool.getSoftware())));

            Ssh.createFile(client, puppetScript, 0600, "/tmp/packages.pp");
            Session session = client.startSession();
            try {
                session.allocateDefaultPTY();
                Session.Command command = session.exec("sudo puppet apply --verbose /tmp/packages.pp");

                Ssh.logCommandOutput(LOG, machine.getExternalId(), command);
                command.join();

                if (command.getExitStatus() != 0) {
                    throw new RuntimeException(String.format("Failed to execute puppet. Exit code: %d. Exit message: %s",
                        command.getExitStatus(), command.getExitErrorMessage()));

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

    private List<Map<String, String>> packagesAsListOfMaps(Software software) {
        ImmutableList.Builder<Map<String, String>> result = ImmutableList.builder();
        for (String pkg : software.getPackages()) {
            result.add(ImmutableMap.of("package", pkg));
        }
        return result.build();
    }
}

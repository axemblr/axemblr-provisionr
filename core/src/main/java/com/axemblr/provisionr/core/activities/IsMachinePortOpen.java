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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check that we can connect to a specific port on a remote machine
 * <p/>
 * This activity expects to find an environment variable named 'machine'
 */
public class IsMachinePortOpen implements JavaDelegate {

    public static final Logger LOG = LoggerFactory.getLogger(IsMachinePortOpen.class);

    private final int timeoutInMilliseconds = 2000;
    private final String resultVariable;
    private final int port;

    public IsMachinePortOpen(String resultVariable, int port) {
        checkArgument(port > 0, "invalid port number");
        this.resultVariable = checkNotNull(resultVariable, "resultVariable is null");
        this.port = port;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Machine machine = (Machine) execution.getVariable("machine");
        checkNotNull(machine, "expecting a process variable named machine (multi-instance?)");

        if (isPortOpen(machine)) {
            LOG.info("<< Port {} is OPEN on {}", port, machine.getPublicDnsName());
            execution.setVariable(resultVariable, true);

        } else {
            LOG.info("<< Port {} is CLOSED on {}", port, machine.getPublicDnsName());
            execution.setVariable(resultVariable, false);
        }
    }

    private boolean isPortOpen(Machine machine) {
        InetSocketAddress socketAddress = new InetSocketAddress(machine.getPublicDnsName(), port);

        Socket socket = null;
        try {
            socket = new Socket();
            socket.setReuseAddress(false);
            socket.setSoLinger(false, 1);
            socket.setSoTimeout(timeoutInMilliseconds);
            socket.connect(socketAddress, timeoutInMilliseconds);

        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioe) {
                    // no work to do
                }
            }
        }
        return true;
    }
}

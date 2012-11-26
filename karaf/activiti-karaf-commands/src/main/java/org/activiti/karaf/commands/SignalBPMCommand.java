/*
 * Copyright 2012 Cisco Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.karaf.commands;

import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 *
 * @author Srinivasan Chikkala
 */
@Command(scope = "act", name = "signal", description = "Signals any active executions in BPMN process instances")
public class SignalBPMCommand extends BPMCommand {

    @Argument(index = 0, name = "instanceIDs", description = "Instance IDs to signal set of active process instances", required = false, multiValued = true)
    private String[] instanceIDs;
    @Option(name = "-a", aliases = "--all", description = "Signal all active process instances")
    private boolean signalAll;
    @Option(name = "-activities", aliases = "--activities", required = false, multiValued = true, description = "Signal all activities in a process instances")
    private String[] activities;

    @Override
    protected Object doExecute() throws Exception {
        ProcessEngine pe = this.getProcessEngine();
        if (pe == null) {
            System.out.println("Process Engine NOT Found!");
            return null;
        }
        RuntimeService rt = pe.getRuntimeService();
        
        if (this.instanceIDs != null && this.instanceIDs.length > 0) {
            for (String instanceID : this.instanceIDs) {
                signal(rt, instanceID, this.activities);
            }
            return null;
        }
        
        if (!signalAll) {
            System.out.println("Process instance IDs required or use the command with -a or --all option");
            return null;            
        } else {
            System.out.println("Signalling all executions in all active process instances...");
            List<ProcessInstance> piList = rt.createProcessInstanceQuery().orderByProcessInstanceId().asc().list();
            for (ProcessInstance pi : piList) {
                signal(rt, pi.getProcessInstanceId(), this.activities);
            }
        }

        return null;
    }

    private void signal(RuntimeService rt, Execution exec) {
        try {
            if (!exec.isEnded()) {
                rt.signal(exec.getId());
            } else {
                System.out.printf("Execution %s already ended \n" + exec.getId());
            }
        } catch (Exception ex) {
            System.out.printf("Exception:%s in signaling the execution %s \n", ex.getMessage(), exec.getId());
        }
    }

    private void signal(RuntimeService rt, String pi, String... activities) {
        if (activities == null || activities.length == 0) {
            // signal all executions in the instance 
            System.out.println("Signaling all active executions in the process instance " + pi);
            List<Execution> executions = rt.createExecutionQuery()
                    .processInstanceId(pi)
                    .orderByProcessInstanceId().asc().list();
            for (Execution exec : executions) {
                signal(rt, exec);
            }
        } else {
            for (String activity : activities) {
                System.out.printf("Signaling activity %s in process instance %s \n", activity, pi);
                List<Execution> executions = rt.createExecutionQuery()
                        .processInstanceId(pi)
                        .activityId(activity)
                        .orderByProcessInstanceId().asc().list();
                for (Execution exec : executions) {
                    signal(rt, exec);
                }
            }
        }
    }
}

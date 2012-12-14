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

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * @author Srinivasan Chikkala
 */
@Command(scope = "activiti", name = "start",
    description = "Starts the Activiti process instance from a deployed process definition")
public class StartActivitiCommand extends ActivitiCommand {

    @Argument(index = 0, name = "definitionID", required = true, multiValued = false,
        description = "Activiti Process definition ID to start an instance of it.")
    private String definitionID;

    @Override
    protected Object doExecute() throws Exception {
        ProcessEngine pe = this.getProcessEngine();
        if (pe == null) {
            System.out.println("Process Engine NOT Found!");
            return null;
        }

        RuntimeService rt = pe.getRuntimeService();
        if (definitionID != null) {
            ProcessInstance pi = rt.startProcessInstanceById(definitionID);
            System.out.printf("Process instance %s Started\n", pi.getProcessInstanceId());
        }

        return null;
    }
}

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

package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.PrintStream;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "provisionr", name = "pools", description = "List active pools")
public class ListPoolsCommand extends OsgiCommandSupport {

    private static final PrintStream out = System.out;

    private final ProcessEngine processEngine;

    public ListPoolsCommand(ProcessEngine processEngine) {
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        List<ProcessInstance> processes = processEngine.getRuntimeService()
            .createProcessInstanceQuery().list();

        if (processes.isEmpty()) {
            out.println("No active pools found. You can create one using provisionr:create");
        }
        for (ProcessInstance instance : processes) {
            Pool pool = (Pool) processEngine.getRuntimeService()
                .getVariable(instance.getId(), CoreProcessVariables.POOL);

            // TODO: try to retrieve the actual list of machines for this pool

            out.println(pool.toString());
            out.println("Business Key: " + instance.getBusinessKey());
            out.println();
        }

        return null;
    }
}

/*
 * Copyright (c) 2013 S.C. Axemblr Software Solutions S.R.L
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


import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import java.io.PrintStream;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "provisionr", name = "resume",
    description = "Resume all suspended Activiti processes identified by the (business) key and associated with a pool")
public class ResumeCommand extends OsgiCommandSupport {

    private final ProcessEngine processEngine;
    private PrintStream out = System.out;

    @Option(name = "-k", aliases = "--key", description = "Key to resume processes", required = true)
    private String businessKey = "";

    public ResumeCommand(ProcessEngine processEngine) {
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        if (businessKey.isEmpty()) {
            out.println("Please supply a business key");
        } else {
            RuntimeService runtimeService = processEngine.getRuntimeService();
            List<ProcessInstance> processInstanceList = processEngine.getRuntimeService()
                .createProcessInstanceQuery().processInstanceBusinessKey(businessKey)
                .orderByProcessInstanceId().list();
            // reverse the list to start the sub-processes first (they have bigger id's)
            out.println("Found " + processInstanceList.size() + " processes with key " + businessKey);
            for (ProcessInstance instance : Lists.reverse(processInstanceList)) {
                if (instance.isSuspended()) {
                    out.println("Activating process with id " + instance.getId());
                    runtimeService.activateProcessInstanceById(instance.getId());
                }
            }
        }
        return null;
    }

    @VisibleForTesting
    void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    @VisibleForTesting
    void setOut(PrintStream out) {
        this.out = out;
    }
}

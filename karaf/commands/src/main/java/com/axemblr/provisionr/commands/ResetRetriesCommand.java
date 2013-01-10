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


import com.axemblr.provisionr.core.CoreProcessVariables;
import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.PrintStream;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "provisionr", name = "reset-retries",
    description = "Reset to default (3) the number of retires of all jobs without retries left.")
public class ResetRetriesCommand extends OsgiCommandSupport {

    private final ProcessEngine processEngine;
    private PrintStream out = System.out;

    @Option(name = "-k", aliases = "--key", description = "Reset number of retries for jobs associated with pool", required = true)
    private String businessKey = "";

    public ResetRetriesCommand(ProcessEngine processEngine) {
        this.processEngine = checkNotNull(processEngine, "processEngine is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        if (businessKey.isEmpty()) {
            out.println("Please supply a business key");
        } else {
            // reverse the list to start the sub-processes first (they have bigger id's)
            List<ProcessInstance> processInstanceList = processEngine.getRuntimeService()
                .createProcessInstanceQuery().variableValueEquals(CoreProcessVariables.POOL_BUSINESS_KEY, businessKey)
                .orderByProcessInstanceId().desc().list();

            out.printf("Found %d processes with pool business key %s%n", processInstanceList.size(), businessKey);
            int count = 0;
            for (ProcessInstance instance : processInstanceList) {
                List<Job> jobs = processEngine.getManagementService().createJobQuery()
                    .processInstanceId(instance.getProcessInstanceId()).withException().list();
                for (Job job : jobs) {
                    count++;
                    processEngine.getManagementService().setJobRetries(job.getId(), JobEntity.DEFAULT_RETRIES);
                }
            }
            out.printf("Number of retries reset for %s jobs", count);
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

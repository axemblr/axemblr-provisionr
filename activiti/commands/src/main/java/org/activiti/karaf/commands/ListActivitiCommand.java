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

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.karaf.commands.util.TextTable;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author Srinivasan Chikkala
 */
@Command(scope = "activiti", name = "list", description = "Displays information about Activiti active " +
    "process instances, process definitions, history of process instances")
public class ListActivitiCommand extends ActivitiCommand {

    @Option(name = "-pi", aliases = "--active", description = "Display information about all active process instances")
    private boolean active;

    @Option(name = "-pd", aliases = "--definitions", description = "Display information about all process definitions")
    private boolean definitions;

    @Option(name = "-h", aliases = "--history", description = "Display information about history of all process instances")
    private boolean history;

    @Option(name = "-d", aliases = "--deployments", description = "Display information about all Activiti deployments")
    private boolean deployments;


    @Override
    protected Object doExecute() throws Exception {
        ProcessEngine pe = this.getProcessEngine();

        if (pe == null) {
            out().println("Process Engine NOT Found!");
            return null;
        }

        if (!(this.active || this.definitions || this.history || this.deployments)) {
            // none of them set, display everything 
            // set all to true;
            this.active = this.definitions = this.history = this.deployments = true;
        }

        if (this.deployments) {
            RepositoryService repo = pe.getRepositoryService();
            printDeployments(out(), repo);
        }

        if (this.definitions) {
            RepositoryService repo = pe.getRepositoryService();
            printProcessDefinitions(out(), repo);
        }

        if (this.history) {
            HistoryService his = pe.getHistoryService();
            boolean printActive = !this.active; // if we show active process, dont print then in history 
            printHistoricProcessInstances(out(), his, printActive);
        }

        if (this.active) {
            RuntimeService rt = pe.getRuntimeService();
            printActiveProcessInstances(out(), rt);
        }


        return null;
    }

    private String formatDate(Date date) {
        String dateTxt = "";
        if (date != null) {
            dateTxt = DateFormat.getDateTimeInstance().format(date);
        }
        return dateTxt;
    }

    private String formatBpmResource(String bpmResource) {
        if (bpmResource.startsWith("bundleresource:")) {
            return bpmResource.substring("bundleresource:".length());
        } else {
            return bpmResource;
        }
    }

    private void printDeployments(PrintWriter out, RepositoryService repo) {

        List<Deployment> depList = repo.createDeploymentQuery().orderByDeploymenTime().asc().list();

        out.println();
        out.println("Activiti Deployments");
        out.println("--------------------");
        if (depList.isEmpty()) {
            out.println("No Activiti Deployments Found.");
            return;
        }

        TextTable txtTable = new TextTable(3);

        txtTable.addHeaders("ID", "Name", "Deployment Time");
        for (Deployment dep : depList) {
            txtTable.addRow(dep.getId(), dep.getName(), formatDate(dep.getDeploymentTime()));
        }
        txtTable.print(out);
    }

    private void printProcessDefinitions(PrintWriter out, RepositoryService repo) {
        List<ProcessDefinition> pdList = repo.createProcessDefinitionQuery()
            .orderByDeploymentId().asc().list();

        out.println();
        out.println("Activiti Process Definitions");
        out.println("----------------------------");
        if (pdList.isEmpty()) {
            out.println("No Activiti Process Definitions Found.");
            return;
        }

        TextTable txtTable = new TextTable(4);

        txtTable.addHeaders("Definition ID", "Name", "Version", "Resource");
        for (ProcessDefinition pd : pdList) {
            Integer ver = pd.getVersion();
            txtTable.addRow(pd.getId(), pd.getName(), ver.toString(), formatBpmResource(pd.getResourceName()));
        }
        txtTable.print(out);
    }

    private String getExecutions(RuntimeService rt, String pi) {
        List<Execution> executions = rt.createExecutionQuery()
            .processInstanceId(pi)
            .orderByProcessInstanceId().asc().list();
        StringBuilder bld = new StringBuilder();
        boolean first = true;
        for (Execution exec : executions) {
            if (!first) {
                bld.append(",");
            } else {
                first = false;
            }
            bld.append(exec.getId());
        }
        return bld.toString();
    }

    private void printActiveProcessInstances(PrintWriter out, RuntimeService rt) {

        List<ProcessInstance> piList = rt.createProcessInstanceQuery().orderByProcessInstanceId().asc().list();

        out.println();
        out.println("Active Process Instances");
        out.println("------------------------");
        if (piList.isEmpty()) {
            out.println("No Active Process Instances Found.");
            return;
        }

        TextTable txtTable = new TextTable(3);

        txtTable.addHeaders("Definition ID", "Instance ID", "Executions");
        for (ProcessInstance pi : piList) {
            txtTable.addRow(pi.getProcessDefinitionId(),
                pi.getProcessInstanceId(), getExecutions(rt, pi.getProcessInstanceId()));
        }
        txtTable.print(out);
    }

    private void printHistoricProcessInstances(PrintWriter out, HistoryService his, boolean printActive) {

        List<HistoricProcessInstance> hpiList = his.createHistoricProcessInstanceQuery()
            .orderByProcessDefinitionId().asc().list();

        out.println();
        out.println("History of Activiti Process Instances");
        out.println("-------------------------------------");
        if (hpiList.isEmpty()) {
            out.println("No History on Activiti Processes.");
            return;
        }

        TextTable txtTable = new TextTable(4);

        txtTable.addHeaders("Definition ID", "Instance ID", "Start Time", "End Time");
        for (HistoricProcessInstance hpi : hpiList) {
            Date endTime = hpi.getEndTime();
            if (endTime == null && !printActive) {
                continue;  // don't print active instance history if printActive is false - default.
            }
            txtTable.addRow(hpi.getProcessDefinitionId(), hpi.getId(),
                formatDate(hpi.getStartTime()), formatDate(hpi.getEndTime()));
        }
        txtTable.print(out);
    }
}

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * karaf command class that prints the details about the bpmn process inlcuding deployment, definition,
 * instance and process varaible details.
 * 
 * @author Srinivasan Chikkala
 */
@Command(scope = "act", name = "info", description = "Provides details about the BPMN process instance")
public class InfoBPMCommand extends BPMCommand {
    private static final Logger LOG = Logger.getLogger(InfoBPMCommand.class.getName());

    @Argument(index = 0, name = "instanceID", description = "Instance ID for which the details should be displayed", required = true, multiValued = false)
    private String instanceID;
    @Option(name = "-v", aliases = "--verbose", description = "Full details of the process instance")
    private boolean verbose;
    @Option(name = "-q", aliases = "--quiet", description = "Show minimun required details of the process instance")
    private boolean quiet;

    private BPMPrintHandler printHandler;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public BPMPrintHandler getPrintHandler() {
        return printHandler;
    }

    public void setPrintHandler(BPMPrintHandler printHandler) {
        this.printHandler = printHandler;
    }

    @Override
    protected Object doExecute() throws Exception {
        Object obj = null;
        try {
            obj = executeCommand();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            LOG.log(Level.INFO, ex.getMessage(), ex);
        }
        return obj;
    }

    protected Object executeCommand() throws Exception {
        ProcessEngine pe = this.getProcessEngine();
        if (pe == null) {
            System.out.println("Process Engine NOT Found!");
            return null;
        }
        if (this.instanceID == null || this.instanceID.trim().length() == 0) {
            System.out.println("Instance ID required to show the information about the instance");
            return null;
        }

        this.printHandler = this.findBPMPrintHandler();

        printDetails(this.instanceID.trim());
        return null;
    }

    protected BPMPrintHandler findBPMPrintHandler() {
        BPMPrintHandler handler = null;
        List<BPMPrintHandler> hList = null;
        try {
            String filter = null; // add the filter here per process.
            hList = this.getAllServices(BPMPrintHandler.class, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hList == null || hList.size() == 0) {
            DefaultBPMPrintHandler defHandler = new DefaultBPMPrintHandler();
            defHandler.setProcessEngine(this.getProcessEngine());
            handler = defHandler;
        } else {
            handler = hList.get(0); // first one that matches.
        }
        return handler;
    }

    protected void printDeploymentInfo(Deployment depInfo) {
        LinkedHashMap<String, String> nvMap = new LinkedHashMap<String, String>();
        nvMap.put("Deployment ID", depInfo.getId());
        nvMap.put("Deployment Name", depInfo.getName());
        nvMap.put("Deployment Time", CmdUtil.UTIL.formatDate(depInfo.getDeploymentTime()));
        CmdUtil.UTIL.printNameValues(new PrintWriter(System.out, true), nvMap);
    }

    protected void printProcessDefinitionInfo(ProcessDefinition pd) {
        LinkedHashMap<String, String> nvMap = new LinkedHashMap<String, String>();
        nvMap.put("Definition ID", pd.getId());
        nvMap.put("Definition Name", pd.getName());
        nvMap.put("Version", Integer.toString(pd.getVersion()));
        nvMap.put("Resource Name", pd.getResourceName());
        CmdUtil.UTIL.printNameValues(new PrintWriter(System.out, true), nvMap);

    }

    protected void printProcessInstanceInfo(HistoricProcessInstance hpi) {
        LinkedHashMap<String, String> nvMap = new LinkedHashMap<String, String>();
        nvMap.put("Instance ID", hpi.getId());
        nvMap.put("Start Activity", hpi.getStartActivityId());
        nvMap.put("End Activity", hpi.getEndActivityId());
        nvMap.put("Start Time", CmdUtil.UTIL.formatDate(hpi.getStartTime()));
        nvMap.put("End Time", CmdUtil.UTIL.formatDate(hpi.getEndTime()));
        if (!this.isQuiet()) {
            nvMap.put("Duration", CmdUtil.UTIL.formatDuration(hpi.getDurationInMillis()));
        }

        PrintWriter out = new PrintWriter(System.out, true);
        CmdUtil.UTIL.printNameValues(out, nvMap);
        // print instance data
        this.getPrintHandler().printInstanceData(out, this.isVerbose(), this.isQuiet(), hpi);
    }

    protected void printActivityInstanceInfo(HistoricActivityInstance actInst) {

        LinkedHashMap<String, String> nvMap = new LinkedHashMap<String, String>();
        nvMap.put("Activity ID", actInst.getActivityId());
        if (!this.isQuiet()) {
            nvMap.put("Activity Type", actInst.getActivityType());
        }
        nvMap.put("Activitiy Name", actInst.getActivityName());
        if (!this.isQuiet()) {
            nvMap.put("Execution ID", actInst.getExecutionId());
        }
        nvMap.put("Start Time", CmdUtil.UTIL.formatDate(actInst.getStartTime()));
        nvMap.put("End Time", CmdUtil.UTIL.formatDate(actInst.getEndTime()));
        if (!this.isQuiet()) {
            nvMap.put("Duration", CmdUtil.UTIL.formatDuration(actInst.getDurationInMillis()));
        }

        PrintWriter out = new PrintWriter(System.out, true);
        CmdUtil.UTIL.printNameValues(out, nvMap);
        // print activity vars
        this.getPrintHandler().printActivityData(out, this.isVerbose(), this.isQuiet(), actInst);
        System.out.println("-------------");
    }

    protected void printDetails(String pid) {
        ProcessEngine pe = this.getProcessEngine();
        RepositoryService repo = pe.getRepositoryService();
        RuntimeService rt = pe.getRuntimeService();
        HistoryService hs = pe.getHistoryService();

        ProcessInstance pi = rt.createProcessInstanceQuery().processInstanceId(pid).singleResult();
        HistoricProcessInstance hpi = hs.createHistoricProcessInstanceQuery().processInstanceId(pid)
                .singleResult();
        if (pi == null && hpi == null) {
            // both null means. no process with that id.
            System.out.printf("No process details found with process id %s \n", pid);
            return;
        }

        String pdId = null;
        if (pi != null) {
            pdId = pi.getProcessDefinitionId();
        } else if (hpi != null) {
            pdId = hpi.getProcessDefinitionId();
        }

        ProcessDefinition pd = repo.createProcessDefinitionQuery().processDefinitionId(pdId).singleResult();
        Deployment depInfo = repo.createDeploymentQuery().deploymentId(pd.getDeploymentId()).singleResult();
        // print
        if (this.isVerbose()) {
            System.out.println("======== Deployment Details");
            printDeploymentInfo(depInfo);
            System.out.println("======== Process Definition Details");
            printProcessDefinitionInfo(pd);
        }

        System.out.println("======== Process Instance Details");
        printProcessInstanceInfo(hpi);

        List<HistoricActivityInstance> actInstList = hs.createHistoricActivityInstanceQuery()
                .processInstanceId(hpi.getId()).orderByHistoricActivityInstanceStartTime().asc().list();
        if (actInstList != null && actInstList.size() > 0) {
            System.out.println("======== Activity Execution Details");
            for (HistoricActivityInstance actInst : actInstList) {
                printActivityInstanceInfo(actInst);
            }
        } else {
            LOG.info("No Activity execution details");
        }
    }

}

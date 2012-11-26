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
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableUpdate;

/**
 * Abstract class that provides most of the implementation required to print process variable using
 * print handler.
 * 
 * @see DefaultBPMPrintHandler
 * 
 * @author Srinivasan Chikkala
 * 
 */
public abstract class AbstractBPMPrintHandler implements BPMPrintHandler {
    // service properties
    public static final String PROCESS_DEFINITION_PROP = "bpm.process.definition";
    public static final String PROCESS_VARS_PROP = "bpm.process.variables";
    //
    private static final Logger LOG = Logger.getLogger(AbstractBPMPrintHandler.class.getName());
    //
    private boolean verbose;
    private boolean quiet;
    private ProcessEngine processEngine;

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    /**
     * extended class can implement this method to print the variable specific information.
     * 
     * @param out
     * @param varName
     * @param varValue
     */
    protected abstract void printVariable(PrintWriter out, String varName, Object varValue);

    protected void printVariable(PrintWriter out, HistoricVariableUpdate var) {

        LinkedHashMap<String, String> nvMap = new LinkedHashMap<String, String>();
        // nvMap.put("Variable Type", var.getVariableTypeName());
        if (this.isVerbose()) {
            nvMap.put("Variable ID", var.getId());
            nvMap.put("Revision", "" + var.getRevision());
            nvMap.put("Updated Time", CmdUtil.UTIL.formatDate(var.getTime()));
        }
        nvMap.put("Variable Name", var.getVariableName());
        Object value = var.getValue();
        String simpleValue = CmdUtil.UTIL.valueOf(value);
        if (simpleValue != null) {
            nvMap.put("Value", simpleValue);
        } else {
            nvMap.put("Value", "");
        }
        CmdUtil.UTIL.printNameValues(out, nvMap);

        if (simpleValue == null) {
            // print the value in a json serialization format.
            printVariable(out, var.getVariableName(), value);
        }
        out.println();
    }

    protected void printVariables(PrintWriter out, Map<String, HistoricVariableUpdate> vars) {
        for (HistoricVariableUpdate var : vars.values()) {
            printVariable(out, var);
        }
    }

    protected void printVariables(PrintWriter out, List<HistoricDetail> varList) {
        Map<String, HistoricVariableUpdate> varMap = new TreeMap<String, HistoricVariableUpdate>();
        // filter revisions
        for (HistoricDetail detail : varList) {
            HistoricVariableUpdate varDetail = (HistoricVariableUpdate) detail;
            String varName = varDetail.getVariableName();
            // expects the varList is sorted in a descending order of time.
            if (!varMap.containsKey(varName)) {
                varMap.put(varName, varDetail);
            } else {
                LOG.info("#### " + varName + " has multiple updates!!! "
                        + CmdUtil.UTIL.formatDate(varDetail.getTime()) + " Revision= "
                        + varDetail.getRevision());
            }
        }
        printVariables(out, varMap);
    }

    @Override
    public void printInstanceData(PrintWriter out, boolean verbose, boolean quite, HistoricProcessInstance hpi) {
        this.setVerbose(verbose);
        this.setQuiet(quite);

        List<HistoricDetail> varList = null;
        try {
            varList = this.processEngine.getHistoryService().createHistoricDetailQuery()
                    .variableUpdates().processInstanceId(hpi.getId()).orderByTime().desc().list();
        } catch (ActivitiException ex) {
            // silent about the error. and log it.
            LOG.log(Level.INFO, "Error in getting process variables. " + ex.getMessage(), ex);
        }        
        if (varList != null && varList.size() > 0) {
            out.println("-------- Instance Variables ");
            printVariables(out, varList);
        } else {
            LOG.info("------ No Instance Variables! for " + hpi.getId());
        }
    }

    @Override
    public void printActivityData(PrintWriter out, boolean verbose, boolean quite,
            HistoricActivityInstance actInst) {
        this.setVerbose(verbose);
        this.setQuiet(quite);
        if (quite) {
            // don't print activity variable update per activity
            return;
        }        
        List<HistoricDetail> varList = null;
        try {
            varList = this.processEngine.getHistoryService().createHistoricDetailQuery()
                    .variableUpdates().activityInstanceId(actInst.getId()).orderByTime().desc().list();
        } catch (ActivitiException ex) {
            //silent about the error. and log it.
            LOG.log(Level.INFO, "Error in getting process variables. " + ex.getMessage(), ex);
        }
        
        if (varList != null && varList.size() > 0) {
            out.println("-------- Task Variables");
            printVariables(out, varList);
        } else {
            LOG.info("------ No Task Variables! for " + actInst.getActivityId());
        }
    }

}

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

package org.activiti.karaf.commands.handlers;

import java.io.PrintWriter;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;

/**
 * Service interface whose implementations can handle process variables printing as part of bpm:info cmd.
 * service providers can register implementation of this interface as a OSGi service which the bpm:info cmd
 * can lookup and use to print a bpmn process specific variable information.
 * <p/>
 * Each OSGi service registered can have the following set of properties
 * process-definition=<process-definition-id> - which handles all process instances variable printing for this
 * process definition.
 * process-var-name=<name-of-the-var> - which handles all process instances that contains this variable name
 * useful when you want to handles any process definitions that contains this variable.
 *
 * @author Srinivasan Chikkala
 */
public interface ActivitiPrintHandler {

    /**
     * Print instance level Activiti process variable data
     */
    void printInstanceData(PrintWriter out, boolean verbose, boolean quiet, HistoricProcessInstance hpi);

    /**
     * Print activity level Activiti process variable data
     */
    void printActivityData(PrintWriter out, boolean verbose, boolean quiet, HistoricActivityInstance actInst);
}

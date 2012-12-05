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

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricProcessInstance;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 * @author Srinivasan Chikkala
 */
@Command(scope = "activiti", name = "clean-history", description = "Removes history of the BPMN process instances")
public class CleanHistoryActivitiCommand extends ActivitiCommand {

    @Argument(index = 0, name = "instanceIDs", description = "Instance IDs to remove from history",
        required = false, multiValued = true)
    private String[] instanceIDs;

    @Option(name = "-a", aliases = "--all", description = "Remove all Activiti Processes from history")
    private boolean cleanAll;

    @Option(name = "-pd", aliases = "--definitions", required = false, multiValued = true,
        description = "Removes history of process instances started from the definitions")
    private String[] definitionIDs;

    @Override
    protected Object doExecute() throws Exception {
        ProcessEngine engine = this.getProcessEngine();
        if (engine == null) {
            System.out.println("Process Engine NOT Found!");
            return null;
        }
        HistoryService historyService = engine.getHistoryService();

        // order of priority if instnaceIDs or definitionIDs and all on the list
        // process instnaceID and exist or process definitionIDs and exit or process all 
        // TODO figure out how to add mutually exclusive options - instanceIDs | definitions | all

        if (this.instanceIDs != null && this.instanceIDs.length > 0) {
            this.cleanProcessInstanceHistory(historyService, this.instanceIDs);
            return null;
        }

        if (this.definitionIDs != null && this.definitionIDs.length > 0) {
            this.cleanProcessDefinitionHistory(historyService, this.definitionIDs);
            return null;
        }

        // clean all history
        if (!cleanAll) {
            System.out.println("Process instance IDs required or use the command with -a " +
                "or --all option to clean all history");
            return null;
        } else {
            cleanAllHistory(historyService);
        }

        return null;
    }

    private void cleanAllHistory(HistoryService hs) {
        System.out.println("Cleaning History of All Process Instances...");
        List<HistoricProcessInstance> hpiList = hs.createHistoricProcessInstanceQuery()
            .orderByProcessDefinitionId().asc().list();
        if (hpiList == null || hpiList.size() == 0) {
            System.out.println("No Process History found! ");
            return;
        }
        for (HistoricProcessInstance hpi : hpiList) {
            String processId = hpi.getId();
            hs.deleteHistoricProcessInstance(hpi.getId());
            System.out.printf("History removed for process instance %s \n", processId);
        }
    }

    private void cleanProcessInstanceHistory(HistoryService hs, String... instances) {
        for (String instanceId : instances) {
            // query and if exists delete.
            HistoricProcessInstance hpi = hs.createHistoricProcessInstanceQuery()
                .processInstanceId(instanceId).singleResult();
            if (hpi != null) {
                hs.deleteHistoricProcessInstance(hpi.getId());
                System.out.printf("History removed for process instance %s \n", hpi.getId());
            } else {
                System.out.printf("No History found for process instance %s \n", instanceId);
            }
        }
    }

    private void cleanProcessDefinitionHistory(HistoryService hs, String... definitions) {

        for (String definitionId : definitions) {
            List<HistoricProcessInstance> hpiList = hs.createHistoricProcessInstanceQuery()
                .processDefinitionId(definitionId)
                .orderByProcessDefinitionId().asc().list();
            if (hpiList == null || hpiList.size() == 0) {
                System.out.printf("No History found for process definition %s \n", definitionId);
                break;
            }
            for (HistoricProcessInstance hpi : hpiList) {
                String processId = hpi.getId();
                hs.deleteHistoricProcessInstance(hpi.getId());
                System.out.printf("History removed for process instance %s with definition %s\n", processId,
                    definitionId);
            }
        }
    }
}

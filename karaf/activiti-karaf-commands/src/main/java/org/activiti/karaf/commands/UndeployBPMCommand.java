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
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

/**
 *
 * @author Srinivasan Chikkala
 */
@Command(scope = "act", name = "undeploy", description = "Undeploys the BPMN Deployments(Process definition, images, bar files etc") 
public class UndeployBPMCommand extends BPMCommand {
    
    @Argument(index=0, name = "deploymentIDs", description = "Deployment IDs of the BPMN deployments for undeploying", required=false, multiValued=true)
    private String[] deploymentIDs;
    
    @Option(name = "-a", aliases = "--all", description = "Undeploys all BPMN deployments ")
    private boolean undeployAll;
    
    @Option(name = "-c", aliases = "--cascade", description = "Deletes the given deployment and cascade deletion to process instances, history process instances and jobs.")
    private boolean cascade;
    
    
    @Override
    protected Object doExecute() throws Exception {
        ProcessEngine pe = this.getProcessEngine();
        if (pe == null) {
            System.out.println("Process Engine NOT Found!");
            return null;
        }
               
        RepositoryService repo = pe.getRepositoryService();
        
       if (this.deploymentIDs != null && this.deploymentIDs.length > 0) {
            for (String deploymentID : this.deploymentIDs) {
                repo.deleteDeployment(deploymentID, this.cascade);
                System.out.printf("Undeployed %s \n", deploymentID);
            }
            return null;
        }
        
        if (!undeployAll) {
            System.out.println("BPMN Deployment IDs required or use the command with -a or --all option for all undeployments");
            return null;            
        } else {
            System.out.println("Undeploying all BPMN deployments...");
            List<Deployment> depList = repo.createDeploymentQuery().orderByDeploymenTime().asc().list();
            for (Deployment dep : depList) {
                String deploymentID = dep.getId();
                repo.deleteDeployment(deploymentID, this.cascade);
                System.out.printf("Undeployed %s \n", deploymentID);
            }
        }
       
        return null;
    }
    
}

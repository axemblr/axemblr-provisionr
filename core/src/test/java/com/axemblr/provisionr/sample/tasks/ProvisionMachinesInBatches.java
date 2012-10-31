package com.axemblr.provisionr.sample.tasks;

import org.activiti.engine.impl.pvm.ProcessDefinitionBuilder;
import org.activiti.engine.impl.pvm.PvmProcessDefinition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

import com.google.common.collect.Lists;

public class ProvisionMachinesInBatches implements ActivityBehavior {

    @Override
    public void execute(ActivityExecution execution) throws Exception {
        // ProcessDefinitionBuilder processBuilder = new
        // ProcessDefinitionBuilder();
        // execution.createSubProcessInstance(processBuilder.buildProcessDefinition()).start();

        System.err.println("** Provisioning machines in batches");
        execution.takeAll(execution.getActivity().getOutgoingTransitions(), Lists.<ActivityExecution> newArrayList());
    }

}

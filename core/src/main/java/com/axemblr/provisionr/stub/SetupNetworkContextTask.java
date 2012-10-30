package com.axemblr.provisionr.stub;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class SetupNetworkContextTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.err.println("** Setup network context for cluster");        
    }

}

package com.axemblr.provisionr.sample.tasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CheckCredentialsTask implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.err.println("** Check cloud provider credentials");
    }
}

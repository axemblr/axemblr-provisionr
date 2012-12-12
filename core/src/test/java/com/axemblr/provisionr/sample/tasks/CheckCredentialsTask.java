package com.axemblr.provisionr.sample.tasks;

import java.util.concurrent.TimeUnit;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class CheckCredentialsTask implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.err.println("** Check cloud provider credentials");
        TimeUnit.SECONDS.sleep(10);
        System.err.println("** Exit Check cloud provider credentials");
    }
}

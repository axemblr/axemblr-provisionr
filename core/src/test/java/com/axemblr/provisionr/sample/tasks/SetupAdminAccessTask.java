package com.axemblr.provisionr.sample.tasks;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class SetupAdminAccessTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        System.err.println("** Setup admin access");
    }
}

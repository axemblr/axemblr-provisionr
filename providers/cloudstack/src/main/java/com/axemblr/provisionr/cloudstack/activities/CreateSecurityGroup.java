package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.pool.Pool;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSecurityGroup implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CreateSecurityGroup.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
//        Pool pool = (Pool) execution.getVariable("pool");
        LOG.info("Retrieved pool");
    }
}

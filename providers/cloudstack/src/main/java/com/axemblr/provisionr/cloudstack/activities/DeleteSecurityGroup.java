package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.core.SecurityGroups;
import org.activiti.engine.delegate.DelegateExecution;
import org.jclouds.cloudstack.CloudStackClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteSecurityGroup extends CloudStackActivity {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteSecurityGroup.class);

    @Override
    public void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution) {
        String securityGroupName = SecurityGroups.formatNameFromBusinessKey(execution.getProcessBusinessKey());
        SecurityGroups.deleteByName(cloudStackClient, securityGroupName);
    }
}

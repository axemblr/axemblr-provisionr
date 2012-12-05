package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.base.Throwables;
import org.activiti.engine.delegate.DelegateExecution;

public class DeleteSecurityGroup extends AmazonActivity {

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        final String groupName = "network-" + execution.getProcessBusinessKey();
        try {
            LOG.info(">> Deleting Security Group {}", groupName);

            client.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupName(groupName));

        } catch (AmazonServiceException e) {
            if (e.getErrorCode().equals("InvalidGroup.NotFound")) {
                LOG.info("<< Security Group {} not found. Ignoring this error.", groupName);
            } else {
                throw Throwables.propagate(e);
            }
        }
    }
}

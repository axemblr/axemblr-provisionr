package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DeleteSecurityGroupRequest;
import com.axemblr.provisionr.amazon.core.ErrorCodes;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.amazon.core.SecurityGroups;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteSecurityGroup extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteSecurityGroup.class);

    public DeleteSecurityGroup(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        final String groupName = SecurityGroups.formatNameFromBusinessKey(execution.getProcessBusinessKey());
        try {
            LOG.info(">> Deleting Security Group {}", groupName);

            client.deleteSecurityGroup(new DeleteSecurityGroupRequest().withGroupName(groupName));

        } catch (AmazonServiceException e) {
            if (e.getErrorCode().equals(ErrorCodes.SECURITY_GROUP_NOT_FOUND)) {
                LOG.info("<< Security Group {} not found. Ignoring this error.", groupName);
            } else {
                throw Throwables.propagate(e);
            }
        }
    }
}

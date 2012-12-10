package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.api.pool.Pool;
import static com.google.common.base.Preconditions.checkNotNull;
import org.activiti.engine.delegate.DelegateExecution;

/**
 * Terminate instances previously started by {@see RunOnDemandInstances}
 */
public class TerminateInstances extends AmazonActivity {

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        String[] instances = (String[]) execution.getVariable(ProcessVariables.INSTANCES);
        checkNotNull(instances, "process variable '{}' not found", ProcessVariables.INSTANCES);

        client.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instances));
    }
}

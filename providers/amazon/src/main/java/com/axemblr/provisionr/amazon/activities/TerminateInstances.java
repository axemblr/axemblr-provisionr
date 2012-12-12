package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Arrays;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Terminate instances previously started by {@see RunOnDemandInstances}
 */
public class TerminateInstances extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(TerminateInstances.class);

    public TerminateInstances(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        String[] instances = (String[]) execution.getVariable(ProcessVariables.INSTANCES);
        checkNotNull(instances, "process variable '{}' not found", ProcessVariables.INSTANCES);

        LOG.info(">> Terminating instances: {}", Arrays.toString(instances));
        client.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instances));
    }
}

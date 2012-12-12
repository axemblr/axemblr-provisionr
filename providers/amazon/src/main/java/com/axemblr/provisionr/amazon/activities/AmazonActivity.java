package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import static com.google.common.base.Preconditions.checkNotNull;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public abstract class AmazonActivity implements JavaDelegate {

    private final ProviderClientCache clientCache;

    protected AmazonActivity(ProviderClientCache clientCache) {
        this.clientCache = checkNotNull(clientCache, "clientCache is null");
    }

    /**
     * Amazon specific activity implementation
     *
     * @param client    Amazon client created using the pool provider
     * @param pool      Virtual machines pool description              
     * @param execution Activiti execution context
     */
    public abstract void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception;

    /**
     * Wrap the abstract {@code execute} method with the logic that knows how to create the Amazon client
     */
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Pool pool = (Pool) execution.getVariable(ProcessVariables.POOL);
        checkNotNull(pool, "Please add the pool description as a process " +
            "variable with the name '%s'.", ProcessVariables.POOL);

        execute(clientCache.getUnchecked(pool.getProvider()), pool, execution);
    }
}

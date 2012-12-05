package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.io.Closeables;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.jclouds.ContextBuilder;
import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.rest.RestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all activities that require access to a CloudStack based cloud.
 */
public abstract class CloudStackActivity implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CloudStackActivity.class);

    /**
     * Implement activity logic in this method. It will be called with a reference to the {@link CloudStackClient}
     *
     * @param cloudStackClient
     * @param pool
     * @param execution
     */
    public abstract void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution);

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        RestContext<CloudStackClient, CloudStackAsyncClient> restContext = null;
        try {
            Pool pool = (Pool) checkNotNull(execution.getVariable("pool"), "Please add 'pool' variable to the process!");
            // delegate
            restContext = newCloudStackClient(pool.getProvider());
            execute(restContext.getApi(), pool, execution);

        } finally {
            Closeables.closeQuietly(restContext);
        }
    }

    /**
     * Creates a new {@link CloudStackClient} with {@link Provider} supplied credentials.
     *
     * @param provider
     * @return
     */
    RestContext<CloudStackClient, CloudStackAsyncClient> newCloudStackClient(Provider provider) {
        return ContextBuilder
            .newBuilder(new CloudStackApiMetadata())
            .endpoint(provider.getEndpoint())
            .credentials(provider.getAccessKey(), provider.getSecretKey())
            .build(CloudStackApiMetadata.CONTEXT_TOKEN);
    }
}

package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.cloudstack.ProcessVariables;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.jclouds.ContextBuilder;
import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
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
            Pool pool = Pool.class.cast(checkNotNull(execution.getVariable(ProcessVariables.POOL),
                "Please add 'pool' variable to the process"));
            // delegate
            restContext = newCloudStackClient(pool.getProvider());
            execute(restContext.getApi(), pool, execution);

        } finally {
            Closeables.closeQuietly(restContext);
        }
    }

    /**
     * Creates a new {@link CloudStackClient} with {@link Provider} supplied credentials.
     */
    RestContext<CloudStackClient, CloudStackAsyncClient> newCloudStackClient(Provider provider) {
        checkArgument(provider.getEndpoint().isPresent(), "please specify an endpoint for this provider");
        return ContextBuilder.newBuilder(new CloudStackApiMetadata())
            .endpoint(provider.getEndpoint().get())
            .modules(ImmutableSet.<Module>of(new SLF4JLoggingModule()))
            .credentials(provider.getAccessKey(), provider.getSecretKey())
            .build(CloudStackApiMetadata.CONTEXT_TOKEN);
    }
}

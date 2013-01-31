package com.axemblr.provisionr.amazon.activities;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.CancelSpotInstanceRequestsRequest;
import com.axemblr.provisionr.amazon.AmazonProvisionr;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;

public class CancelSpotRequests extends AmazonActivity {

    public static final Logger LOG = LoggerFactory.getLogger(AmazonProvisionr.class);

    public CancelSpotRequests(ProviderClientCache providerClientCache) {
        super(providerClientCache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        @SuppressWarnings("unchecked")
        List<String> requests = (List<String>) execution.getVariable(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS);
        checkNotNull(requests, "process variable '{}' not found", ProcessVariables.SPOT_INSTANCE_REQUEST_IDS);
        try {
            if (requests.size() > 0) {
                client.cancelSpotInstanceRequests(new CancelSpotInstanceRequestsRequest()
                        .withSpotInstanceRequestIds(requests));
            }
        } catch (AmazonServiceException exception) {
            LOG.warn("There was an error cancelling your spot instance requests: {}", exception);
            throw exception;
        }
    }    
}

package com.axemblr.provisionr.amazon.activities;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;

public class GetInstanceIdsFromSpotRequests extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(GetInstanceIdsFromSpotRequests.class);
    
    public GetInstanceIdsFromSpotRequests(ProviderClientCache providerClientCache) {
        super(providerClientCache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        LOG.info(">> retrieving instance Ids from spot request Ids");

        @SuppressWarnings("unchecked")
        List<String> requestIds = 
                (List<String>) execution.getVariable(ProcessVariables.SPOT_INSTANCE_REQUEST_IDS);
        DescribeSpotInstanceRequestsResult result = client.describeSpotInstanceRequests(
                new DescribeSpotInstanceRequestsRequest().withSpotInstanceRequestIds(requestIds));
        List<String> instanceIds = new ArrayList<String>();
        for (SpotInstanceRequest spotRequest : result.getSpotInstanceRequests()) {
            if (spotRequest.getInstanceId() != null) {
                instanceIds.add(spotRequest.getInstanceId());
            }
        }
        execution.setVariable(ProcessVariables.INSTANCE_IDS, instanceIds);
    }

}

package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.ProviderOptions;
import com.axemblr.provisionr.amazon.SoftwareOptions;
import com.axemblr.provisionr.amazon.core.ImageTable;
import com.axemblr.provisionr.amazon.core.ImageTableQuery;
import com.axemblr.provisionr.amazon.core.KeyPairs;
import com.axemblr.provisionr.amazon.core.SecurityGroups;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;

public class RunOnDemandInstances extends AmazonActivity {

    public static final String DEFAULT_ARCH = "amd64";
    public static final String DEFAULT_TYPE = "instance-store";

    private ImageTable table;

    public RunOnDemandInstances() throws IOException {
        this.table = ImageTable.fromCsvResource("amis/ubuntu.csv");
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        final String businessKey = execution.getProcessBusinessKey();

        final String securityGroupName = SecurityGroups.formatNameFromBusinessKey(businessKey);
        final String keyPairName = KeyPairs.formatNameFromBusinessKey(businessKey);

        final String instanceType = pool.getHardware().getType();
        final String imageId = queryImageTableForId(pool.getProvider(), instanceType);

        final RunInstancesRequest request = new RunInstancesRequest()
            .withClientToken(businessKey)
            .withSecurityGroups(securityGroupName)
            .withKeyName(keyPairName)
            .withInstanceType(instanceType)
            .withImageId(imageId)
            .withMinCount(pool.getMinSize())
            .withMaxCount(pool.getExpectedSize());

        // TODO allow for more options (e.g. monitoring & termination protection etc.)

        LOG.info(">> Sending RunInstances request: {}", request);
        RunInstancesResult result = client.runInstances(request);

        // TODO tag instances: managed-by: Axemblr Provisionr, business-key: ID etc.

        LOG.info("<< RunInstances result: {}", result);
        execution.setVariable(ProcessVariables.RESERVATION_ID,
            result.getReservation().getReservationId());
        execution.setVariable(ProcessVariables.INSTANCES,
            collectInstanceIdsAsArray(result.getReservation().getInstances()));
    }

    private String queryImageTableForId(Provider provider, String instanceType) {
        final String region = provider.getOptionOr(ProviderOptions.REGION, ProviderOptions.DEFAULT_REGION);
        final String version = provider.getOptionOr(SoftwareOptions.BASE_OPERATING_SYSTEM_VERSION,
            SoftwareOptions.DEFAULT_BASE_OPERATING_SYSTEM_VERSION);

        ImageTableQuery query = table.query()
            .filterBy("region", region)
            .filterBy("version", version)
            .filterBy("arch", DEFAULT_ARCH);

        if (instanceType.equals("t1.micro")) {
            query.filterBy("type", "ebs");
        } else {
            query.filterBy("type", DEFAULT_TYPE);
        }

        return query.singleResult();
    }

    private String[] collectInstanceIdsAsArray(List<Instance> instances) {
        List<String> ids = Lists.newArrayList();
        for (Instance instance : instances) {
            ids.add(instance.getInstanceId());
        }
        return ids.toArray(new String[ids.size()]);
    }
}

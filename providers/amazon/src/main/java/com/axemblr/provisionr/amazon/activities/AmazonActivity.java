package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeRegionsRequest;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AmazonActivity implements JavaDelegate {

    public static final Logger LOG = LoggerFactory.getLogger(AmazonActivity.class);

    public static final String AXEMBLR_USER_AGENT = "Axemblr Provisionr aws-sdk-java/1.3.14";
    public static final String DEFAULT_REGION = "us-east-1";

    /**
     * Amazon specific activity implementation
     *
     * @param client    Amazon client created using the pool provider
     * @param pool      Virtual machines pool description
     * @param execution Activiti execution context
     */
    public abstract void execute(AmazonEC2 client, Pool pool, DelegateExecution execution);

    /**
     * Wrap the abstract {@code execute} method with the logic that knows how to create the Amazon client
     */
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Pool pool = (Pool) execution.getVariable("pool");
        checkNotNull(pool, "Please add the pool description as a process " +
            "variable with the name 'pool'.");

        AmazonEC2 client = null;
        try {
            client = newAmazonEc2Client(pool.getProvider());
            execute(client, pool, execution);
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    /**
     * Create a new Amazon EC2 client using the provider details
     */
    AmazonEC2 newAmazonEc2Client(Provider provider) {
        String region = Optional.fromNullable(provider.getOptions().get("region")).or(DEFAULT_REGION);

        AWSCredentials credentials = new BasicAWSCredentials(provider.getAccessKey(), provider.getSecretKey());
        AmazonEC2 client = new AmazonEC2Client(credentials, new ClientConfiguration()
            .withUserAgent(AXEMBLR_USER_AGENT));

        if (provider.getEndpoint().isPresent()) {
            LOG.info(">> Using endpoint {} as configured", provider.getEndpoint().get());
            client.setEndpoint(provider.getEndpoint().get());

        } else {
            LOG.info(">> Searching endpoint for region {}", region);
            DescribeRegionsRequest request = new DescribeRegionsRequest().withRegionNames(region);

            DescribeRegionsResult result = client.describeRegions(request);
            checkArgument(result.getRegions().size() == 1, "Invalid region name %s. Expected one result found %s",
                region, result.getRegions());

            LOG.info("<< Using endpoint {} for region {}", result.getRegions().get(0).getEndpoint(), region);
            client.setEndpoint(result.getRegions().get(0).getEndpoint());
        }

        return client;
    }
}

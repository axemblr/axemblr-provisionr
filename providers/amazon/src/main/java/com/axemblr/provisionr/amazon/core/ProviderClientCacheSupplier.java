package com.axemblr.provisionr.amazon.core;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeRegionsRequest;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.axemblr.provisionr.amazon.ProviderOptions;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.ForwardingLoadingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderClientCacheSupplier implements Supplier<ProviderClientCache> {

    private static final Logger LOG = LoggerFactory.getLogger(ProviderClientCacheSupplier.class);

    public static final String AXEMBLR_USER_AGENT = "Axemblr Provisionr aws-sdk-java/1.3.14";

    public static final int MAX_CACHE_SIZE = 100;
    public static final int EXPIRE_AFTER_WRITE_IN_HOURS = 2;

    private static class ProviderClientCacheLoader extends CacheLoader<Provider, AmazonEC2> {

        @Override
        public AmazonEC2 load(Provider provider) throws Exception {
            String region = Optional.fromNullable(provider.getOptions().get(ProviderOptions.REGION))
                .or(ProviderOptions.DEFAULT_REGION);

            AWSCredentials credentials = new BasicAWSCredentials(provider.getAccessKey(), provider.getSecretKey());
            AmazonEC2 client = new AmazonEC2Client(credentials, new ClientConfiguration()
                .withUserAgent(AXEMBLR_USER_AGENT));

            if (provider.getEndpoint().isPresent()) {
                LOG.info("Using endpoint {} as configured", provider.getEndpoint().get());
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

    public static class WrapLoadingCacheAsProviderClientCache extends ForwardingLoadingCache<Provider, AmazonEC2>
        implements ProviderClientCache {

        private final LoadingCache<Provider, AmazonEC2> delegate;

        public WrapLoadingCacheAsProviderClientCache(LoadingCache<Provider, AmazonEC2> delegate) {
            this.delegate = checkNotNull(delegate, "delegate is null");
        }

        @Override
        protected LoadingCache<Provider, AmazonEC2> delegate() {
            return delegate;
        }
    }

    @Override
    public ProviderClientCache get() {
        final LoadingCache<Provider, AmazonEC2> cache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterWrite(EXPIRE_AFTER_WRITE_IN_HOURS, TimeUnit.HOURS)
            .removalListener(new RemovalListener<Provider, AmazonEC2>() {
                @Override
                public void onRemoval(RemovalNotification<Provider, AmazonEC2> notification) {
                    LOG.info("Closing client for provider: {}", notification.getKey());
                    notification.getValue().shutdown();
                }
            })
            .build(new ProviderClientCacheLoader());

        return new WrapLoadingCacheAsProviderClientCache(cache);
    }
}

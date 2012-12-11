package com.axemblr.provisionr.amazon.config;

import com.axemblr.provisionr.amazon.AmazonProvisionr;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultProviderConfig {

    private final String accessKey;
    private final String secretKey;
    private final String region;

    public DefaultProviderConfig(String accessKey, String secretKey, String region) {
        this.accessKey = checkNotNull(accessKey, "accessKey is null");
        this.secretKey = checkNotNull(secretKey, "secretKey is null");
        this.region = checkNotNull(region, "region is null");
    }

    public Optional<Provider> createProvider() {
        if (accessKey.isEmpty() || secretKey.isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(Provider.builder().id(AmazonProvisionr.ID).accessKey(accessKey)
            .secretKey(secretKey).option("region", region).createProvider());
    }
}

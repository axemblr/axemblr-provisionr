package com.axemblr.provisionr.api.provider;

import com.axemblr.provisionr.api.util.BuilderWithOptions;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;

public class ProviderBuilder extends BuilderWithOptions<ProviderBuilder> {

    private String id;
    private Optional<String> endpoint = Optional.absent();
    private String accessKey;
    private String secretKey;

    @Override
    protected ProviderBuilder getThis() {
        return this;
    }

    public ProviderBuilder id(String id) {
        this.id = checkNotNull(id, "id is null");
        return this;
    }

    public ProviderBuilder endpoint(Optional<String> endpoint) {
        this.endpoint = checkNotNull(endpoint, "endpoint is null");
        return this;
    }

    public ProviderBuilder endpoint(String endpoint) {
        this.endpoint = Optional.of(endpoint);
        return this;
    }

    public ProviderBuilder accessKey(String accessKey) {
        this.accessKey = checkNotNull(accessKey, "accessKey is null");
        return this;
    }

    public ProviderBuilder secretKey(String secretKey) {
        this.secretKey = checkNotNull(secretKey, "secretKey is null");
        return this;
    }

    public Provider createProvider() {
        return new Provider(id, endpoint, accessKey, secretKey, buildOptions());
    }
}
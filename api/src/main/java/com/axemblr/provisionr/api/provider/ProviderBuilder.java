package com.axemblr.provisionr.api.provider;

import com.axemblr.provisionr.api.util.BuilderWithOptions;

public class ProviderBuilder extends BuilderWithOptions<ProviderBuilder> {

    private String id;
    private String endpoint;
    private String accessKey;
    private String secretKey;

    @Override
    protected ProviderBuilder getThis() {
        return this;
    }

    public ProviderBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ProviderBuilder endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public ProviderBuilder accessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public ProviderBuilder secretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public Provider createProvider() {
        return new Provider(id, endpoint, accessKey, secretKey, buildOptions());
    }
}
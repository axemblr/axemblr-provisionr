package com.axemblr.provisionr.api.provider;

public class ProviderBuilder {

    private String id;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String locationId;

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

    public ProviderBuilder locationId(String locationId) {
        this.locationId = locationId;
        return this;
    }

    public Provider createProvider() {
        return new Provider(id, endpoint, accessKey, secretKey, locationId);
    }
}
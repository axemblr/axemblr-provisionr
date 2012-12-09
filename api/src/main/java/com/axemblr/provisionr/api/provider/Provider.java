package com.axemblr.provisionr.api.provider;

import com.axemblr.provisionr.api.util.WithOptions;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;

public class Provider extends WithOptions {

    public static ProviderBuilder builder() {
        return new ProviderBuilder();
    }

    private final String id;
    private final Optional<String> endpoint;

    private final String accessKey;
    private final String secretKey;

    Provider(String id, Optional<String> endpoint, String accessKey,
             String secretKey, Map<String, String> options) {
        super(options);
        this.id = checkNotNull(id, "id is null");
        this.endpoint = checkNotNull(endpoint, "endpoint is null");
        this.accessKey = checkNotNull(accessKey, "accessKey is null");
        this.secretKey = checkNotNull(secretKey, "secretKey is null");
    }

    /**
     * Unique provider ID
     */
    public String getId() {
        return id;
    }

    /**
     * API endpoint for this provider
     */
    public Optional<String> getEndpoint() {
        return endpoint;
    }

    /**
     * API access key
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * API secret key
     */
    public String getSecretKey() {
        return secretKey;
    }

    public ProviderBuilder toBuilder() {
        return builder().id(id).endpoint(endpoint).accessKey(accessKey)
            .secretKey(secretKey).options(getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, endpoint, accessKey, secretKey, getOptions());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Provider other = (Provider) obj;
        return Objects.equal(this.id, other.id)
            && Objects.equal(this.endpoint, other.endpoint)
            && Objects.equal(this.accessKey, other.accessKey)
            && Objects.equal(this.secretKey, other.secretKey)
            && Objects.equal(this.getOptions(), other.getOptions());
    }

    @Override
    public String toString() {
        return "Provider{" +
            "id='" + id + '\'' +
            ", endpoint='" + endpoint.or("") + '\'' +
            ", accessKey='" + accessKey + '\'' +
            ", options='" + getOptions() + '\'' +
            '}';
    }
}

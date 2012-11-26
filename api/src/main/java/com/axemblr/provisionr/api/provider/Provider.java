package com.axemblr.provisionr.api.provider;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;

public class Provider implements Serializable {

    public static ProviderBuilder builder() {
        return new ProviderBuilder();
    }

    private final String id;
    private final String endpoint;

    private final String accessKey;
    private final String secretKey;

    private final Map<String, String> options;

    public Provider(String id, String endpoint, String accessKey,
                    String secretKey, Map<String, String> options) {
        this.id = checkNotNull(id, "id is null");
        this.endpoint = Optional.fromNullable(endpoint).or("");
        this.accessKey = Optional.fromNullable(accessKey).or("");
        this.secretKey = Optional.fromNullable(secretKey).or("");
        this.options = ImmutableMap.copyOf(options);
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
    public String getEndpoint() {
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

    /**
     * Generic provider configuration options
     */
    public Map<String, String> getOptions() {
        return options;
    }

    public ProviderBuilder toBuilder() {
        return builder().id(id).endpoint(endpoint).accessKey(accessKey)
            .secretKey(secretKey).options(options);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, endpoint, accessKey, secretKey, options);
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
        return Objects.equal(this.id, other.id) && Objects.equal(this.endpoint, other.endpoint)
            && Objects.equal(this.accessKey, other.accessKey) && Objects.equal(this.secretKey, other.secretKey)
            && Objects.equal(this.options, other.options);
    }

    @Override
    public String toString() {
        return "Provider{" +
            "id='" + id + '\'' +
            ", endpoint='" + endpoint + '\'' +
            ", accessKey='" + accessKey + '\'' +
            ", options='" + options + '\'' +
            '}';
    }
}

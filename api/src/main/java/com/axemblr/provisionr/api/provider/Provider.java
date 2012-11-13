package com.axemblr.provisionr.api.provider;

import com.google.common.base.Objects;
import java.io.Serializable;

public class Provider implements Serializable {

    public static ProviderBuilder builder() {
        return new ProviderBuilder();
    }

    private final String id;
    private final String endpoint;

    private final String accessKey;
    private final String secretKey;

    private final String locationId;

    public Provider(String id, String endpoint, String accessKey,
                    String secretKey, String locationId) {
        this.id = id;
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.locationId = locationId;
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
     * Target location ID
     */
    public String getLocationId() {
        return locationId;
    }

    public ProviderBuilder toBuilder() {
        return builder().id(id).endpoint(endpoint).accessKey(accessKey)
            .secretKey(secretKey).locationId(locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, endpoint, accessKey, secretKey, locationId);
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
            && Objects.equal(this.locationId, other.locationId);
    }

    @Override
    public String toString() {
        return "Provider{" +
            "id='" + id + '\'' +
            ", endpoint='" + endpoint + '\'' +
            ", accessKey='" + accessKey + '\'' +
            ", locationId='" + locationId + '\'' +
            '}';
    }
}

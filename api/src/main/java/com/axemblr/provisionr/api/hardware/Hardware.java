package com.axemblr.provisionr.api.hardware;

import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;

public class Hardware implements Serializable {

    public static HardwareBuilder builder() {
        return new HardwareBuilder();
    }

    private final String type;

    private final Map<String, String> options;

    Hardware(String type, Map<String, String> options) {
        this.type = checkNotNull(type, "type is null");
        this.options = ImmutableMap.copyOf(options);
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public HardwareBuilder toBuilder() {
        return builder().type(type).options(options);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, options);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Hardware other = (Hardware) obj;
        return Objects.equal(this.type, other.type)
            && Objects.equal(this.options, other.options);
    }

    @Override
    public String toString() {
        return "Hardware{" +
            "type='" + type + '\'' +
            ", options=" + options +
            '}';
    }
}

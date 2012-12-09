package com.axemblr.provisionr.api.hardware;

import com.axemblr.provisionr.api.util.WithOptions;
import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;

public class Hardware extends WithOptions {

    public static HardwareBuilder builder() {
        return new HardwareBuilder();
    }

    private final String type;

    Hardware(String type, Map<String, String> options) {
        super(options);
        this.type = checkNotNull(type, "type is null");
    }

    public String getType() {
        return type;
    }

    public HardwareBuilder toBuilder() {
        return builder().type(type).options(getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, getOptions());
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
            && Objects.equal(this.getOptions(), other.getOptions());
    }

    @Override
    public String toString() {
        return "Hardware{" +
            "type='" + type + '\'' +
            ", options=" + getOptions() +
            '}';
    }
}

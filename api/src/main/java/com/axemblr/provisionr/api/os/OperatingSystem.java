package com.axemblr.provisionr.api.os;

import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class OperatingSystem implements Serializable {

    public static OperatingSystemBuilder builder() {
        return new OperatingSystemBuilder();
    }

    private final String type;

    private final Map<String, String> files;

    private final Set<String> aptKeys;
    private final Set<String> packages;

    private final Map<String, String> options;

    public OperatingSystem(String type, Map<String, String> files, Set<String> aptKeys, Set<String> packages,
                           Map<String, String> options) {
        this.type = checkNotNull(type, "type is null");
        this.files = ImmutableMap.copyOf(files);
        this.aptKeys = ImmutableSet.copyOf(aptKeys);
        this.packages = ImmutableSet.copyOf(packages);
        this.options = ImmutableMap.copyOf(options);
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getFiles() {
        return files;
    }

    public Set<String> getAptKeys() {
        return aptKeys;
    }

    public Set<String> getPackages() {
        return packages;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public OperatingSystemBuilder toBuilder() {
        return builder().type(type).files(files).aptKeys(aptKeys).packages(packages).options(options);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, files, aptKeys, packages, options);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final OperatingSystem other = (OperatingSystem) obj;
        return Objects.equal(this.type, other.type) && Objects.equal(this.files, other.files)
            && Objects.equal(this.aptKeys, other.aptKeys) && Objects.equal(this.packages, other.packages)
            && Objects.equal(this.options, other.options);
    }

    @Override
    public String toString() {
        return "OperatingSystem{" +
            "type='" + type + '\'' +
            ", files=" + files +
            ", aptKeys=" + aptKeys +
            ", packages=" + packages +
            ", options=" + options +
            '}';
    }
}

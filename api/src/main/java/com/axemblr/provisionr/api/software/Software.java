package com.axemblr.provisionr.api.software;

import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Define the software environment for all the machines in the pool
 */
public class Software implements Serializable {

    public static SoftwareBuilder builder() {
        return new SoftwareBuilder();
    }

    private final String baseOperatingSystem;

    private final Map<String, String> files;
    private final List<String> packages;

    private final Map<String, String> options;

    Software(String baseOperatingSystem, Map<String, String> files, List<String> packages,
             Map<String, String> options) {
        this.baseOperatingSystem = checkNotNull(baseOperatingSystem, "baseOperatingSystem is null");
        this.files = ImmutableMap.copyOf(files);
        this.packages = ImmutableList.copyOf(packages);
        this.options = ImmutableMap.copyOf(options);
    }

    public String getBaseOperatingSystem() {
        return baseOperatingSystem;
    }

    /**
     * Map of remote files that need to be available on the local filesystem
     */
    public Map<String, String> getFiles() {
        return files;
    }

    /**
     * List of packages that should be installed
     * <p/>
     * This list can also include paths to local files
     */
    public List<String> getPackages() {
        return packages;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public SoftwareBuilder toBuilder() {
        return builder().baseOperatingSystem(baseOperatingSystem).files(files).packages(packages).options(options);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(baseOperatingSystem, files, packages, options);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final Software other = (Software) obj;
        return Objects.equal(this.baseOperatingSystem, other.baseOperatingSystem) && Objects.equal(this.files, other.files)
            && Objects.equal(this.packages, other.packages) && Objects.equal(this.options, other.options);
    }

    @Override
    public String toString() {
        return "Software{" +
            "baseOperatingSystem='" + baseOperatingSystem + '\'' +
            ", files=" + files +
            ", packages=" + packages +
            ", options=" + options +
            '}';
    }
}

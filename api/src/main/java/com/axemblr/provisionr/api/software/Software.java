/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axemblr.provisionr.api.software;

import com.axemblr.provisionr.api.util.WithOptions;
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
public class Software extends WithOptions {

    public static SoftwareBuilder builder() {
        return new SoftwareBuilder();
    }

    private final String baseOperatingSystem;

    private final Map<String, String> files;
    private final List<String> packages;

    Software(String baseOperatingSystem, Map<String, String> files, List<String> packages,
             Map<String, String> options) {
        super(options);
        this.baseOperatingSystem = checkNotNull(baseOperatingSystem, "baseOperatingSystem is null");
        this.files = ImmutableMap.copyOf(files);
        this.packages = ImmutableList.copyOf(packages);
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


    public SoftwareBuilder toBuilder() {
        return builder().baseOperatingSystem(baseOperatingSystem).files(files)
            .packages(packages).options(getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(baseOperatingSystem, files, packages, getOptions());
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
        return Objects.equal(this.baseOperatingSystem, other.baseOperatingSystem)
            && Objects.equal(this.files, other.files)
            && Objects.equal(this.packages, other.packages)
            && Objects.equal(this.getOptions(), other.getOptions());
    }

    @Override
    public String toString() {
        return "Software{" +
            "baseOperatingSystem='" + baseOperatingSystem + '\'' +
            ", files=" + files +
            ", packages=" + packages +
            ", options=" + getOptions() +
            '}';
    }
}

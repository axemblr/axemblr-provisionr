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
import java.util.List;
import java.util.Map;

/**
 * Define the software environment for all the machines in the pool
 */
public class Software extends WithOptions {

    public static SoftwareBuilder builder() {
        return new SoftwareBuilder();
    }

    private final String imageId;
    private final boolean cachedImage;

    private final Map<String, String> files;
    private final List<String> packages;
    private final List<Repository> repositories;

    Software(String imageId, boolean cachedImage, Map<String, String> files, List<String> packages,
             List<Repository> repositories, Map<String, String> options) {
        super(options);
        this.imageId = checkNotNull(imageId, "The supplied imageId was null");
        this.cachedImage = cachedImage;
        this.files = ImmutableMap.copyOf(files);
        this.packages = ImmutableList.copyOf(packages);
        this.repositories = ImmutableList.copyOf(repositories);
    }

    public String getImageId() {
        return imageId;
    }

    public boolean isCachedImage() {
        return cachedImage;
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

    /**
     * List of custom repositories to add to the system before
     * installing any packages
     */
    public List<Repository> getRepositories() {
        return repositories;
    }

    public SoftwareBuilder toBuilder() {
        return builder().imageId(imageId).files(files)
            .packages(packages).repositories(repositories).options(getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(imageId, cachedImage, files, packages, repositories, getOptions());
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
        return Objects.equal(this.imageId, other.imageId)
            && Objects.equal(this.cachedImage, other.cachedImage)
            && Objects.equal(this.files, other.files)
            && Objects.equal(this.packages, other.packages)
            && Objects.equal(this.repositories, other.repositories)
            && Objects.equal(this.getOptions(), other.getOptions());
    }

    @Override
    public String toString() {
        return "Software{" +
            "imageId='" + imageId + '\'' +
            ", cachedImage=" + cachedImage +
            ", files=" + files +
            ", packages=" + packages +
            ", repositories=" + repositories +
            ", options=" + getOptions() +
            '}';
    }
}

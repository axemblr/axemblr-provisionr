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

import com.axemblr.provisionr.api.util.BuilderWithOptions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Map;

public class SoftwareBuilder extends BuilderWithOptions<SoftwareBuilder> {

    private String baseOperatingSystem = "default";

    private ImmutableMap.Builder<String, String> files = ImmutableMap.builder();
    private ImmutableList.Builder<String> packages = ImmutableList.builder();
    private ImmutableList.Builder<Repository> repositories = ImmutableList.builder();

    @Override
    protected SoftwareBuilder getThis() {
        return this;
    }

    public SoftwareBuilder baseOperatingSystem(String baseOperatingSystem) {
        this.baseOperatingSystem = checkNotNull(baseOperatingSystem, "baseOperatingSystem");
        return this;
    }

    public SoftwareBuilder files(Map<String, String> files) {
        this.files = ImmutableMap.<String, String>builder().putAll(files);
        return this;
    }

    public SoftwareBuilder file(String sourceUrl, String destinationPath) {
        this.files.put(sourceUrl, destinationPath);
        return this;
    }

    public SoftwareBuilder packages(Iterable<String> packages) {
        this.packages = ImmutableList.<String>builder().addAll(packages);
        return this;
    }

    public SoftwareBuilder packages(String... packages) {
        this.packages = ImmutableList.<String>builder().addAll(Lists.newArrayList(packages));
        return this;
    }

    public SoftwareBuilder repositories(Iterable<Repository> repositories) {
        this.repositories = ImmutableList.<Repository>builder().addAll(repositories);
        return this;
    }

    public SoftwareBuilder repository(Repository repository) {
        this.repositories.add(repository);
        return this;
    }

    public Software createSoftware() {
        return new Software(baseOperatingSystem, files.build(), packages.build(),
            repositories.build(), buildOptions());
    }
}
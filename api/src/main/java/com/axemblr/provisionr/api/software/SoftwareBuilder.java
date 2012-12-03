package com.axemblr.provisionr.api.software;

import com.axemblr.provisionr.api.util.BuilderWithOptions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;

public class SoftwareBuilder extends BuilderWithOptions<SoftwareBuilder> {

    private String baseOperatingSystem = "default";

    private ImmutableMap.Builder<String, String> files = ImmutableMap.builder();
    private ImmutableList.Builder<String> packages = ImmutableList.builder();

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

    public SoftwareBuilder packages(List<String> packages) {
        this.packages = ImmutableList.<String>builder().addAll(packages);
        return this;
    }

    public SoftwareBuilder packages(String... packages) {
        this.packages = ImmutableList.<String>builder().addAll(Lists.newArrayList(packages));
        return this;
    }

    public Software createSoftware() {
        return new Software(baseOperatingSystem, files.build(), packages.build(), buildOptions());
    }
}
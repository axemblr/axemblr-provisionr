package com.axemblr.provisionr.api.os;

import com.axemblr.provisionr.api.util.BuilderWithOptions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class OperatingSystemBuilder extends BuilderWithOptions<OperatingSystemBuilder> {

    private String type = "default";
    private ImmutableMap.Builder<String, String> files = ImmutableMap.builder();
    private ImmutableSet.Builder<String> aptKeys = ImmutableSet.builder();
    private ImmutableSet.Builder<String> packages = ImmutableSet.builder();

    @Override
    protected OperatingSystemBuilder getThis() {
        return this;
    }

    public OperatingSystemBuilder type(String type) {
        this.type = checkNotNull(type, "type");
        return this;
    }

    public OperatingSystemBuilder files(Map<String, String> files) {
        this.files = ImmutableMap.<String, String>builder().putAll(files);
        return this;
    }

    public OperatingSystemBuilder file(String sourceUrl, String destinationPath) {
        this.files.put(sourceUrl, destinationPath);
        return this;
    }

    public OperatingSystemBuilder aptKeys(Set<String> aptKeys) {
        this.aptKeys = ImmutableSet.<String>builder().addAll(aptKeys);
        return this;
    }

    public OperatingSystemBuilder aptKey(String url) {
        this.aptKeys.add(url);
        return this;
    }

    public OperatingSystemBuilder packages(Set<String> packages) {
        this.packages = ImmutableSet.<String>builder().addAll(packages);
        return this;
    }

    public OperatingSystemBuilder packages(String... packages) {
        this.packages = ImmutableSet.<String>builder().addAll(Lists.newArrayList(packages));
        return this;
    }

    public OperatingSystem createOperatingSystem() {
        return new OperatingSystem(type, files.build(),
            aptKeys.build(), packages.build(), buildOptions());
    }
}
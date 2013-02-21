package com.axemblr.provisionr.api.hardware;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.axemblr.provisionr.api.util.BuilderWithOptions;

public class BlockDeviceBuilder extends BuilderWithOptions<BlockDeviceBuilder> {

    private Integer size;

    @Override
    protected BlockDeviceBuilder getThis() {
        return this;
    }

    public BlockDeviceBuilder size(int size) {
        checkArgument(size > 0, "The block device size must be a positive integer.");
        this.size = size;
        return this;
    }

    public BlockDevice createBlockDevice() {
        checkNotNull(size, "The size was not specified");
        return new BlockDevice(size, buildOptions());
    }
}

package com.axemblr.provisionr.api.hardware;

import com.axemblr.provisionr.api.util.BuilderWithOptions;
import static com.google.common.base.Preconditions.checkNotNull;

public class HardwareBuilder extends BuilderWithOptions<HardwareBuilder> {

    private String type = "default";

    @Override
    protected HardwareBuilder getThis() {
        return this;
    }

    public HardwareBuilder type(String type) {
        this.type = checkNotNull(type, "type is null");
        return this;
    }

    public Hardware createHardware() {
        return new Hardware(type, buildOptions());
    }
}

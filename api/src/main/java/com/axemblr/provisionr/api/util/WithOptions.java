package com.axemblr.provisionr.api.util;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.Map;

public abstract class WithOptions implements Serializable {

    private final Map<String, String> options;

    public WithOptions(Map<String, String> options) {
        this.options = ImmutableMap.copyOf(options);
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public String getOption(String key) {
        return options.get(checkNotNull(key, "key is null"));
    }

    public String getOptionOr(String key, String value) {
        return Optional.fromNullable(getOption(key)).or(value);
    }
}

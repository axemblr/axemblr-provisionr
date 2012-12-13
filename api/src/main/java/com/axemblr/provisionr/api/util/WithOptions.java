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

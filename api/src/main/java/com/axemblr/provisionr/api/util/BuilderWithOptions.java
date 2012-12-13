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

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Base class useful for creating builders for objects that accept
 * an arbitrary map of options (key, value string pairs)
 */
public abstract class BuilderWithOptions<P extends BuilderWithOptions<P>> {

    private ImmutableMap.Builder<String, String> options = ImmutableMap.builder();

    /**
     * Override this method to return 'this' to allow builder method chaining
     */
    protected abstract P getThis();

    /**
     * Replace options with the ones supplied as argument
     */
    public P options(Map<String, String> options) {
        this.options = ImmutableMap.<String, String>builder().putAll(options);
        return getThis();
    }

    /**
     * Replace options with the ones extracted from the Properties object
     */
    public P options(Properties properties) {
        this.options = ImmutableMap.builder();
        for (String key : properties.stringPropertyNames()) {
            options.put(key, properties.getProperty(key));
        }
        return getThis();
    }

    /**
     * Load options from a properties file available as a resource on the classpath
     */
    public P optionsFromResource(String resource) {
        Properties properties = new Properties();
        ByteArrayInputStream in = null;
        try {
            in = new ByteArrayInputStream(Resources.toByteArray(Resources.getResource(resource)));
            properties.load(in);
            return options(properties);

        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            Closeables.closeQuietly(in);
        }
    }

    /**
     * Add a new option
     */
    public P option(String key, String value) {
        this.options.put(key, value);
        return getThis();
    }

    protected Map<String, String> buildOptions() {
        return options.build();
    }
}

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

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class RepositoryBuilder {

    private String name;
    private ImmutableList.Builder<String> entries = ImmutableList.builder();

    private Optional<String> key = Optional.absent();

    public RepositoryBuilder name(String name) {
        this.name = checkNotNull(name, "name is null");
        return this;
    }

    public RepositoryBuilder entries(List<String> entries) {
        this.entries = ImmutableList.<String>builder().addAll(entries);
        return this;
    }

    public RepositoryBuilder addEntry(String entry) {
        this.entries.add(entry);
        return this;
    }

    public RepositoryBuilder key(Optional<String> key) {
        this.key = checkNotNull(key, "key is null");
        return this;
    }

    public RepositoryBuilder key(String key) {
        this.key = Optional.of(key);
        return this;
    }

    public Repository createRepository() {
        return new Repository(name, entries.build(), key);
    }
}
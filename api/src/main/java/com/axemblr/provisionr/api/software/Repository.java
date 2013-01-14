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

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;

public class Repository implements Serializable {

    public static RepositoryBuilder builder() {
        return new RepositoryBuilder();
    }

    private final String name;
    private final List<String> entries;

    private final Optional<String> key;

    Repository(String name, List<String> entries, Optional<String> key) {
        this.name = checkNotNull(name, "name is null");
        this.entries = ImmutableList.copyOf(entries);
        this.key = checkNotNull(key, "key is null");
    }

    /**
     * The name of the repository
     */
    public String getName() {
        return name;
    }

    /**
     * List of entries (text lines) used to describe this repository
     */
    public List<String> getEntries() {
        return entries;
    }

    /**
     * PGP key used for package signing. This should be the actual key
     * and not a link to an external resource
     */
    public Optional<String> getKey() {
        return key;
    }

    public RepositoryBuilder toBuilder() {
        return builder().name(name).entries(entries).key(key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, entries, key);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Repository other = (Repository) obj;
        return Objects.equal(this.name, other.name) && Objects.equal(this.entries, other.entries)
            && Objects.equal(this.key, other.key);
    }

    @Override
    public String toString() {
        return "Repository{" +
            "name='" + name + '\'' +
            ", entries=" + entries +
            ", key='" + key + '\'' +
            '}';
    }
}

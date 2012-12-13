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

package com.axemblr.provisionr.amazon.core;

import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Map;

public class ImageTableQuery {

    private final ImageTable table;
    private final ImmutableMap.Builder<String, String> filtersBuilder = ImmutableMap.builder();

    public ImageTableQuery(ImageTable table) {
        this.table = checkNotNull(table, "table is null");
    }

    public ImageTableQuery filterBy(String column, String value) {
        filtersBuilder.put(column, value);
        return this;
    }

    public String singleResult() {
        final Map<String, String> filters = filtersBuilder.build();
        final ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        for (String row : table.getTable().rowKeySet()) {
            if (matchesFilters(table.getTable().row(row), filters)) {
                builder.add(table.getTable().get(row, "ami-id"));
            }
        }
        return Iterables.getOnlyElement(builder.build());
    }

    boolean matchesFilters(final Map<String, String> row, Map<String, String> filters) {
        return Iterables.all(filters.entrySet(),
            new Predicate<Map.Entry<String, String>>() {
                @Override
                public boolean apply(Map.Entry<String, String> entry) {
                    return Objects.equal(row.get(entry.getKey()), entry.getValue());
                }
            });
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(table, filtersBuilder);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImageTableQuery other = (ImageTableQuery) obj;
        return Objects.equal(this.table, other.table) && Objects.equal(this.filtersBuilder, other.filtersBuilder);
    }

    @Override
    public String toString() {
        return "ImageTableQuery{" +
            "table=" + table +
            ", filters=" + filtersBuilder +
            '}';
    }
}

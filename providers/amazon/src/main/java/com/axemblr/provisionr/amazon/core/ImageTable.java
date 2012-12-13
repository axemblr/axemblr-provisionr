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

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImageTable {

    private static final Splitter COMMA = Splitter.on(",").trimResults();

    /**
     * Load the list of AMIs from a resource file (csv format)
     * <p/>
     * Note: the parser is doing only split by comma. There is no
     * support for escaping line components
     *
     * @param resource path to resource
     * @return an instance of {@see ImageTable}
     * @throws IOException
     */
    public static ImageTable fromCsvResource(String resource) throws IOException {
        checkNotNull(resource, "resource is null");

        List<String> lines = Resources.readLines(Resources.getResource(ImageTable.class, resource), Charsets.UTF_8);
        checkArgument(!lines.isEmpty(), "the resource is an empty file");

        final ImmutableTable.Builder<String, String, String> table = ImmutableTable.builder();
        final Iterable<String> headers = extractHeaders(lines);

        int index = 0;
        for (String line : Iterables.skip(lines, 1)) {
            final Iterable<Table.Cell<String, String, String>> cells =
                combineHeadersWithLinePartsAsTableCells(index, headers, COMMA.split(line));
            for (Table.Cell<String, String, String> cell : cells) {
                table.put(cell);
            }
            index++;
        }

        return new ImageTable(table.build());
    }

    static Iterable<Table.Cell<String, String, String>> combineHeadersWithLinePartsAsTableCells(
        int index, Iterable<String> headers, Iterable<String> lineParts
    ) {
        final String rowKey = "" + index;
        return transform(zip(headers, lineParts),
            new Function<Map.Entry<String, String>, Table.Cell<String, String, String>>() {
                @Override
                public Table.Cell<String, String, String> apply(Map.Entry<String, String> entry) {
                    return Tables.immutableCell(rowKey, entry.getKey(), entry.getValue());
                }
            });
    }

    static <K, V> Iterable<Map.Entry<K, V>> zip(Iterable<K> first, Iterable<V> second) {
        checkArgument(Iterables.size(first) == Iterables.size(second), "iterables don't have the same size");

        final Iterator<K> iterator = first.iterator();
        return newArrayList(transform(second, new Function<V, Map.Entry<K, V>>() {
            @Override
            public Map.Entry<K, V> apply(V input) {
                return Maps.immutableEntry(iterator.next(), input);
            }
        }));
    }

    static Iterable<String> extractHeaders(List<String> lines) {
        String headerLine = Iterables.getFirst(lines, "");
        checkArgument(!headerLine.isEmpty(), "Found an empty header line");

        return COMMA.split(headerLine);
    }

    private final Table<String, String, String> table;

    public ImageTable(Table<String, String, String> table) {
        this.table = checkNotNull(table, "table is null");
    }

    Table<String, String, String> getTable() {
        return table;
    }

    public ImageTableQuery query() {
        return new ImageTableQuery(this);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(table);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImageTable other = (ImageTable) obj;
        return Objects.equal(this.table, other.table);
    }

    @Override
    public String toString() {
        return "ImageTable{" +
            "table=" + table +
            '}';
    }
}

package com.axemblr.provisionr.amazon.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.IOException;
import java.util.Map;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class ImageTableTest {

    @Test
    public void testLoadAmiTableFromResource() throws IOException {
        ImageTable table = ImageTable.fromCsvResource("amis/ubuntu.csv");

        String id = table.query().filterBy("region", "us-east-1").filterBy("version", "12.04 LTS")
            .filterBy("arch", "amd64").filterBy("type", "instance-store").singleResult();

        assertThat(id).isEqualTo("ami-9a873ff3");
    }

    @Test
    public void testExtractHeaderLine() throws Exception {
        Iterable<String> headers = ImageTable.extractHeaders(ImmutableList.of("a,b,c", "1,2,3"));
        assertThat(headers).containsAll(ImmutableList.of("a", "b", "c"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCombineHeadersWithLinePartsAsTableCells() {
        final ImmutableList<String> headers = ImmutableList.of("a", "b");
        final ImmutableList<String> lineParts = ImmutableList.of("1", "2");

        Iterable<Table.Cell<String, String, String>> cells =
            ImageTable.combineHeadersWithLinePartsAsTableCells(0, headers, lineParts);

        assertThat(cells).contains(Tables.immutableCell("0", "a", "1"));
    }

    @Test
    public void testZipIterators() {
        Iterable<Map.Entry<String, String>> entries = ImageTable.zip(
            ImmutableList.of("a", "b"), ImmutableList.of("1", "2"));

        assertThat(entries).containsAll(ImmutableList.of(
            Maps.immutableEntry("a", "1"), Maps.immutableEntry("b", "2")));
    }
}

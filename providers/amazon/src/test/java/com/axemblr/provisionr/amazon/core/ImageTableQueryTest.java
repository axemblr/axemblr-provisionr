package com.axemblr.provisionr.amazon.core;

import java.io.IOException;
import org.junit.Test;

public class ImageTableQueryTest {

    public final ImageTable table;

    public ImageTableQueryTest() throws IOException {
        this.table = ImageTable.fromCsvResource("amis/ubuntu.csv");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQueryFailsWithMultipleResults() {
        table.query().filterBy("region", "us-east-1").singleResult();
    }
}

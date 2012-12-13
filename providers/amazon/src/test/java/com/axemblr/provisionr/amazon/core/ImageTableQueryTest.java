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

import java.io.IOException;
import org.junit.Test;

public class ImageTableQueryTest {

    public final ImageTable table;

    public ImageTableQueryTest() throws IOException {
        this.table = ImageTable.fromCsvResource("/com/axemblr/provisionr/amazon/ubuntu.csv");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQueryFailsWithMultipleResults() {
        table.query().filterBy("region", "us-east-1").singleResult();
    }
}

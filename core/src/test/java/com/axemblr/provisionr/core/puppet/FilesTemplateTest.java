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

package com.axemblr.provisionr.core.puppet;

import com.axemblr.provisionr.core.Mustache;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class FilesTemplateTest {

    @Test
    public void testFilesTemplate() throws IOException {
        String content = Mustache.toString(FilesTemplateTest.class,
            "/com/axemblr/provisionr/core/puppet/files.pp.mustache",
            ImmutableMap.of("files", toListOfMaps(ImmutableMap.of(
                "http://bin.axemblr.com/test.tar.gz", "/opt/test.tar.gz",
                "http://google.com", "/opt/google.html"
            ))));

        assertThat(content)
            .contains("download_file {\"/opt/test.tar.gz\":\n" +
                "  uri => \"http://bin.axemblr.com/test.tar.gz\"\n" +
                "}")
            .contains("download_file {\"/opt/google.html\":\n" +
                "  uri => \"http://google.com\"\n" +
                "}");
    }

    private List<Map<String, String>> toListOfMaps(Map<String, String> files) {
        return Lists.newArrayList(Iterables.transform(files.entrySet(),
            new Function<Map.Entry<String, String>, Map<String, String>>() {
                @Override
                public Map<String, String> apply(Map.Entry<String, String> entry) {
                    return ImmutableMap.of("source", entry.getKey(),
                        "destination", entry.getValue());
                }
            }));
    }
}

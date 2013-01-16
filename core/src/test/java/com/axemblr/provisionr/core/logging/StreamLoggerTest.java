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

package com.axemblr.provisionr.core.logging;

import com.axemblr.provisionr.core.Ssh;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.util.List;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class StreamLoggerTest {

    private static final Logger LOG = LoggerFactory.getLogger(Ssh.class);

    @Test
    public void testStreamLogger() throws InterruptedException {
        final List<String> lines = Lists.newCopyOnWriteArrayList();

        final byte[] bytes = "line1\nline2\nline3".getBytes(Charsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        final StreamLogger logger = new StreamLogger(inputStream, LOG, MarkerFactory.getMarker("test")) {
            @Override
            protected void log(Logger logger, Marker marker, String line) {
                logger.info(marker, line);  /* just for visual inspection */
                lines.add(line);
            }
        };
        logger.start();

        logger.join();
        assertThat(lines).contains("line1", "line2", "line3");
    }

}

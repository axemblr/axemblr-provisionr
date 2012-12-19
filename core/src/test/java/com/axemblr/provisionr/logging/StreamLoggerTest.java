package com.axemblr.provisionr.logging;

import com.axemblr.provisionr.core.Ssh;
import com.axemblr.provisionr.core.logging.StreamLogger;
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

        ByteArrayInputStream inputStream = new ByteArrayInputStream("line1\nline2\nline3".getBytes());
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

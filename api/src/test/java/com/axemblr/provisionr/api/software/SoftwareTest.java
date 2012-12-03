package com.axemblr.provisionr.api.software;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import org.junit.Test;

public class SoftwareTest {

    @Test
    public void testSerialization() {
        Software os = Software.builder()
            .type("ubuntu-10.04")
            .packages("vim", "git-core")
            .file("http://bin.axemblr.com/something.tar.gz", "/root/something.tar.gz")
            .option("provider", "specific")
            .createSoftware();

        assertSerializable(os, Software.class);
    }
}

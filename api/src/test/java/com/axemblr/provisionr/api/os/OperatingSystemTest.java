package com.axemblr.provisionr.api.os;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import org.junit.Test;

public class OperatingSystemTest {

    @Test
    public void testSerialization() {
        OperatingSystem os = OperatingSystem.builder()
            .type("ubuntu-10.04")
            .packages("vim", "git-core")
            .file("http://bin.axemblr.com/something.tar.gz", "/root/something.tar.gz")
            .option("provider", "specific")
            .createOperatingSystem();

        assertSerializable(os, OperatingSystem.class);
    }
}

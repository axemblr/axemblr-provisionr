package com.axemblr.provisionr.api.software;

import static com.axemblr.provisionr.api.AssertSerializable.assertSerializable;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class SoftwareTest {

    @Test
    public void testSerialization() {
        Software software = Software.builder()
            .baseOperatingSystem("ubuntu-10.04")
            .packages("vim", "git-core")
            .file("http://bin.axemblr.com/something.tar.gz", "/root/something.tar.gz")
            .option("provider", "specific")
            .createSoftware();

        assertThat(software.getBaseOperatingSystem()).isEqualTo("ubuntu-10.04");
        assertThat(software.toBuilder().createSoftware()).isEqualTo(software);

        assertSerializable(software, Software.class);
    }
}

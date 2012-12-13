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

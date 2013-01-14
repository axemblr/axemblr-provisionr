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

public class RepositoryTest {

    @Test
    public void testSerialization() {
        Repository repository = Repository.builder()
            .name("bigtop")
            .addEntry("deb http://bigtop.s3.amazonaws.com/releases/0.5.0/ubuntu/lucid/x86_64  bigtop contrib")
            .key("-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                "Version: GnuPG v1.4.10 (GNU/Linux)\n" +
                "\n" +
                "[....]")
            .createRepository();

        assertThat(repository.toBuilder().createRepository()).isEqualTo(repository);
        assertSerializable(repository, Repository.class);
    }
}

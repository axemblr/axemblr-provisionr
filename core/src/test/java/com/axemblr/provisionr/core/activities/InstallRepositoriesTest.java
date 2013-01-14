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

package com.axemblr.provisionr.core.activities;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.software.Repository;
import com.axemblr.provisionr.api.software.Software;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InstallRepositoriesTest {

    @Test
    public void testCreatePuppetScript() throws Exception {
        Repository repository = Repository.builder()
            .name("bigtop")
            .addEntry("deb http://bigtop.s3.amazonaws.com/releases/0.5.0/ubuntu/lucid/x86_64  bigtop contrib")
            .key("-----BEGIN PGP PUBLIC KEY BLOCK-----\n" +
                "Version: GnuPG v1.4.10 (GNU/Linux)\n" +
                "\n" +
                "[....]")
            .createRepository();

        Pool pool = mock(Pool.class);
        when(pool.getSoftware()).thenReturn(Software.builder().repository(repository).createSoftware());

        PuppetActivity activity = new InstallRepositories();
        String content = activity.createPuppetScript(pool, null);

        assertThat(content).contains("apt::repository { \"bigtop\":\n" +
            "  content => \"deb http://bigtop.s3.amazonaws.com/releases/0.5.0/ubuntu/lucid/x86_64  bigtop contrib\\n\",\n" +
            "  key => \"-----BEGIN PGP PUBLIC KEY BLOCK-----\\nVersion: GnuPG v1.4.10 (GNU/Linux)\\n\\n[....]\"\n" +
            "}");
    }

}

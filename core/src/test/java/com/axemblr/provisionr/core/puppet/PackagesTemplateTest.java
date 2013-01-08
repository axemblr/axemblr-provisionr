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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class PackagesTemplateTest {

    @Test
    public void testPackagesTemplate() throws IOException {
        String content = Mustache.toString(PackagesTemplateTest.class,
            "/com/axemblr/provisionr/core/puppet/packages.pp.mustache",
            ImmutableMap.of("packages", packages("git-core", "vim")));

        assertThat(content).isEqualTo(
            "\n# Ensure all packages listed bellow are installed\n" +
                "Package { ensure => \"installed\" }\n" +
                "\n" +
                "package { \"git-core\": }\n" +
                "package { \"vim\": }\n"
        );
    }

    private List<Map<String, String>> packages(String... packages) {
        ImmutableList.Builder<Map<String, String>> result = ImmutableList.builder();
        for (String pkg : packages) {
            result.add(ImmutableMap.of("package", pkg));
        }
        return result.build();
    }
}

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

import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.software.Repository;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.core.Mustache;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;

public class InstallRepositories extends PuppetActivity {

    public static final String REPOSITORIES_TEMPLATE =
        "/com/axemblr/provisionr/core/puppet/repositories.pp.mustache";

    public InstallRepositories() {
        super("repositories");
    }

    @Override
    public String createPuppetScript(Pool pool, Machine machine) throws Exception {
        return Mustache.toString(getClass(), REPOSITORIES_TEMPLATE,
            ImmutableMap.<String, List<Map<String, String>>>of(
                "repositories", repositoriesAsListOfMaps(pool.getSoftware())));
    }

    private List<Map<String, String>> repositoriesAsListOfMaps(Software software) {
        return Lists.transform(software.getRepositories(), new Function<Repository, Map<String, String>>() {
            @Override
            public Map<String, String> apply(Repository repository) {
                ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder()
                    .put("name", repository.getName())
                    .put("content", Joiner.on("\\n").join(repository.getEntries()));

                if (repository.getKey().isPresent()) {
                    builder.put("key", repository.getKey().get().replace("\n", "\\n"));
                }
                return builder.build();
            }
        });
    }
}

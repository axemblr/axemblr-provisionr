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
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.core.Mustache;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;

public class DownloadFiles extends PuppetActivity {

    public static final String FILES_TEMPLATE = "/com/axemblr/provisionr/core/puppet/files.pp.mustache";

    public DownloadFiles() {
        super("files");
    }

    @Override
    public String createPuppetScript(Pool pool, Machine machine) throws Exception {
        return Mustache.toString(InstallPackages.class, FILES_TEMPLATE,
            ImmutableMap.of("files", filesAsListOfMaps(pool.getSoftware())));
    }

    private List<Map<String, String>> filesAsListOfMaps(Software software) {
        return Lists.newArrayList(Iterables.transform(software.getFiles().entrySet(),
            new Function<Map.Entry<String, String>, Map<String, String>>() {
                @Override
                public Map<String, String> apply(Map.Entry<String, String> entry) {
                    return ImmutableMap.of("source", entry.getKey(), "destination", entry.getValue());
                }
            }));
    }
}

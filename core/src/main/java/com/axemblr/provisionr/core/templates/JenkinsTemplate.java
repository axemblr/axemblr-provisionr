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

package com.axemblr.provisionr.core.templates;

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.software.Repository;
import com.axemblr.provisionr.api.software.Software;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import java.io.IOException;

/**
 * Jenkins template for debian based operating systems
 */
public class JenkinsTemplate implements PoolTemplate {

    public static final String JENKINS_KEY = "/com/axemblr/provisionr/core/templates/jenkins-ci.org.key";

    public static final int JENKINS_PORT = 8080;

    @Override
    public String getId() {
        return "jenkins";
    }

    @Override
    public String getDescription() {
        return "A short template that installs the latest jenkins with git";
    }

    @Override
    public Pool apply(Pool pool) {
        try {
            final String key = Resources.toString(Resources.getResource(
                JenkinsTemplate.class, JENKINS_KEY), Charsets.UTF_8);

            final Repository repository = Repository.builder()
                .name("jenkins")
                .addEntry("deb http://pkg.jenkins-ci.org/debian binary/")
                .key(key).createRepository();

            final Software software = pool.getSoftware().toBuilder()
                .addPackage("jenkins").addPackage("git-core")
                .repository(repository).createSoftware();

            final Network network = pool.getNetwork().toBuilder()
                .addRules(Rule.builder().anySource().tcp().port(JENKINS_PORT).createRule())
                .createNetwork();

            return pool.toBuilder().network(network).software(software).createPool();

        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}

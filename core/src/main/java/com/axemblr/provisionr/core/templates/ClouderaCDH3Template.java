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
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.software.Repository;
import com.axemblr.provisionr.api.software.Software;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import java.io.IOException;

/**
 * Cloudera CDH3 template for Ubuntu lucid
 */
public class ClouderaCDH3Template implements PoolTemplate {

    public static final String CLOUDERA_MANAGER_URL =
        "http://archive.cloudera.com/cm4/installer/latest/cloudera-manager-installer.bin";

    public static final String CLOUDERA_MANAGER_REMOTE_PATH = "/opt/cloudera-manager-installer.bin";

    public static final String UBUNTU_VERSION = "10.04 LTS";

    @Override
    public String getId() {
        return "cloudera-cdh3";
    }

    @Override
    public String getDescription() {
        return "Cloudera CDH3 template for Ubuntu 10.04 LTS (lucid) including Cloudera Manager";
    }

    @Override
    public Pool apply(Pool pool) {
        String key;
        try {
            key = Resources.toString(Resources.getResource(ClouderaCDH3Template.class,
                "/com/axemblr/provisionr/core/templates/cloudera.key"), Charsets.UTF_8);

        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        Repository cdh3Repository = Repository.builder()
            .name("cloudera-cdh3").key(key)
            .addEntry("deb http://archive.cloudera.com/debian lucid-cdh3 contrib")
            .createRepository();

        Repository clouderaManagerRepository = Repository.builder()
            .name("cloudera-cm4").key(key)
            .addEntry("deb http://archive.cloudera.com/cm4/ubuntu/lucid/amd64/cm lucid-cm4 contrib")
            .addEntry("deb-src http://archive.cloudera.com/cm4/ubuntu/lucid/amd64/cm lucid-cm4 contrib")
            .createRepository();

        Software software = pool.getSoftware().toBuilder()
            .repository(cdh3Repository)
            .repository(clouderaManagerRepository)
            .file(CLOUDERA_MANAGER_URL, CLOUDERA_MANAGER_REMOTE_PATH)
            .addPackage("cloudera-manager-agent").addPackage("cloudera-manager-daemons")
            .addPackage("oracle-j2sdk1.6").addPackage("hadoop-0.20").addPackage("hadoop-0.20-native")
            .addPackage("hadoop-hive").addPackage("hadoop-pig").addPackage("oozie-client")
            .addPackage("oozie").addPackage("hue-plugins").addPackage("hue-common")
            .addPackage("hue-proxy").addPackage("hue-about").addPackage("hue-help")
            .addPackage("hue-filebrowser").addPackage("hue-jobsub").addPackage("hue-beeswax")
            .addPackage("hue-useradmin").addPackage("hue-shell").addPackage("hue")
            .createSoftware();

        /* CDH3 has binaries only for lucid not for precise */
        Provider provider = pool.getProvider().toBuilder()
            .option("version", UBUNTU_VERSION).createProvider();

        Network network = pool.getNetwork().toBuilder()
            .addRules(
                Rule.builder().anySource().tcp().port(7180).createRule(), // Cloudera Manager
                Rule.builder().anySource().tcp().port(8888).createRule(), // Hue
                Rule.builder().anySource().tcp().port(8080).createRule()  // Hue
            ).createNetwork();

        return pool.toBuilder().provider(provider).network(network)
            .software(software).createPool();
    }
}

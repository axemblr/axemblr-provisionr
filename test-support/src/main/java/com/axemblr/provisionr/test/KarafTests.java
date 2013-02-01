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

package com.axemblr.provisionr.test;

import java.io.File;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.keepRuntimeFolder;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import org.apache.karaf.tooling.exam.options.LogLevelOption;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.scanFeatures;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.SystemPropertyOption;
import org.ops4j.pax.exam.options.extra.FeaturesScannerProvisionOption;

/**
 * Helper methods for Karaf integration tests
 */
public class KarafTests {

    public static final String KARAF_GROUP_ID = "org.apache.karaf";
    public static final String KARAF_ARTIFACT_ID = "apache-karaf";

    private KarafTests() {
    }

    /**
     * Use the same Karaf version from the project for integration testing with Pax Exam
     */
    public static DefaultCompositeOption useDefaultKarafAsInProjectWithJunitBundles() {
        String karafVersion = getKarafVersionAsInProject();

        MavenArtifactUrlReference karafUrl = maven().groupId(KARAF_GROUP_ID)
            .artifactId(KARAF_ARTIFACT_ID)
            .version(karafVersion)
            .type("tar.gz");

        return new DefaultCompositeOption()
            .add(karafDistributionConfiguration()
                .frameworkUrl(karafUrl)
                .karafVersion(karafVersion)
                .name("Apache Karaf")
                .unpackDirectory(new File("target/exam")))
            .add(keepRuntimeFolder())
            .add(junitBundles())
            .add(logLevel(LogLevelOption.LogLevel.INFO));
    }

    public static String getKarafVersionAsInProject() {
        return MavenUtils.asInProject().getVersion(KARAF_GROUP_ID, KARAF_ARTIFACT_ID);
    }

    /**
     * Make sure all system properties with a given prefix are also available inside the container
     */
    public static DefaultCompositeOption passThroughAllSystemPropertiesWithPrefix(String prefix) {
        DefaultCompositeOption options = new DefaultCompositeOption();
        for (String name : System.getProperties().stringPropertyNames()) {
            if (name.startsWith(prefix)) {
                options.add(systemProperty(name).value(System.getProperty(name)));
            }
        }
        return options;
    }

    /**
     * Set the project version as a Karaf system property
     * <p/>
     * This method assumes that the test-support bundle has the same
     * version as the code being tested
     */
    public static SystemPropertyOption projectVersionAsSystemProperty() {
        String version = MavenUtils.asInProject()
            .getVersion("com.axemblr.provisionr", "provisionr-test-support");
        return systemProperty("project.version").value(version);
    }

    /**
     * Create an option to install the test support bundle inside the PAX Exam container
     */
    public static FeaturesScannerProvisionOption installProvisionrTestSupportBundle() {
        return scanFeatures(
            maven().groupId("com.axemblr.provisionr").artifactId("provisionr-test-support")
                .type("xml").classifier("features").versionAsInProject(),
            "provisionr-test-support"
        );
    }

    /**
     * Create an option to install all the requested Provisionr features inside the PAX Exam container
     */
    public static FeaturesScannerProvisionOption installProvisionrFeatures(String... features) {
        return scanFeatures(
            maven().groupId("com.axemblr.provisionr").artifactId("provisionr-features")
                .type("xml").classifier("features").versionAsInProject(),
            features
        );
    }
}

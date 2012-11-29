package com.axemblr.provisionr.test;

import java.io.File;
import org.apache.karaf.tooling.exam.options.KarafDistributionBaseConfigurationOption;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.SystemPropertyOption;

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
    public static KarafDistributionBaseConfigurationOption useDefaultKarafAsInProject() {
        String karafVersion = MavenUtils.asInProject().getVersion(KARAF_GROUP_ID, KARAF_ARTIFACT_ID);

        MavenArtifactUrlReference karafUrl = maven().groupId(KARAF_GROUP_ID)
            .artifactId(KARAF_ARTIFACT_ID)
            .version(karafVersion)
            .type("tar.gz");

        return karafDistributionConfiguration()
            .frameworkUrl(karafUrl)
            .karafVersion(karafVersion)
            .name("Apache Karaf")
            .unpackDirectory(new File("target/exam"));
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
}

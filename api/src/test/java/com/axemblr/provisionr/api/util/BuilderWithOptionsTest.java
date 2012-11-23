package com.axemblr.provisionr.api.util;

import com.google.common.collect.ImmutableMap;
import java.util.Properties;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class BuilderWithOptionsTest {

    public static class TestBuilder extends BuilderWithOptions<TestBuilder> {
        @Override
        protected TestBuilder getThis() {
            return this;
        }
    }

    @Test
    public void testSetOptionsFromMap() {
        TestBuilder builder = new TestBuilder().options(ImmutableMap.of("K1", "V1", "K2", "V2"));
        assertThat(builder.buildOptions()).containsKey("K2").containsValue("V1");
    }

    @Test
    public void testSetSingleOption() {
        TestBuilder builder = new TestBuilder().option("K1", "V1").option("K2", "V2");
        assertThat(builder.buildOptions()).containsKey("K1").containsValue("V2");
    }

    @Test
    public void testLoadOptionsFromProperties() {
        Properties properties = new Properties();
        properties.setProperty("K1", "V1");

        TestBuilder builder = new TestBuilder().options(properties);
        assertThat(builder.buildOptions()).containsKey("K1").containsValue("V1");
    }

    @Test
    public void testLoadOptionsFromResource() {
        TestBuilder builder = new TestBuilder().optionsFromResource("builder.properties");
        assertThat(builder.buildOptions()).containsKey("key1").containsValue("value2");
    }
}

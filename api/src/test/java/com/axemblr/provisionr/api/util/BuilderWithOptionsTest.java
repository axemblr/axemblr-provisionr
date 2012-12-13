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

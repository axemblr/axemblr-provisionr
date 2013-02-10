/*
 * Copyright (c) 2013 S.C. Axemblr Software Solutions S.R.L
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

package com.axemblr.provisionr.rundeck;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import java.io.IOException;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import org.junit.Test;

/**
 * A test to make sure the JAXB serialization works as expected
 */
public class ProjectTest extends BaseJaxbTest {

    @Test
    public void testEmptyProject() throws Exception {
        String actual = asXml(new Project());
        assertXMLEqual(actual, "<project />", actual);
    }

    @Test
    public void testSimpleProject() throws Exception {
        Project project = new Project();

        Node node = new Node("db-1", "db-1.example.com", "psql");
        node.setTags(new String[]{"a", "b"});
        node.setAttributes(ImmutableMap.of("key1", "val1", "key2", "val2"));

        project.addNodes(node);
        project.addNodes(new Node("web-1", "web-1.example.com", "django"));

        String actual = asXml(project);
        assertXMLEqual(actual, readResource("fixtures/project.xml"), actual);
    }

    private String readResource(String resource) throws IOException {
        return Resources.toString(Resources.getResource(resource), Charsets.UTF_8);
    }

    @Override
    public Class[] getContextClasses() {
        return new Class[]{Project.class};
    }
}

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

package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.core.templates.PoolTemplate;
import com.axemblr.provisionr.core.templates.xml.XmlTemplate;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.felix.service.command.CommandSession;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class ListTemplatesCommandTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream out;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        out = new PrintStream(outputStream);
    }

    @After
    public void tearDown() throws IOException {
        out.close();
        outputStream.close();
    }

    @Test
    public void testListTemplates() throws Exception {
        final ImmutableList<PoolTemplate> templates = ImmutableList.<PoolTemplate>of(
            XmlTemplate.newXmlTemplate(readDefaultTemplate("jenkins")),
            XmlTemplate.newXmlTemplate(readDefaultTemplate("cdh3")));

        ListTemplatesCommand command = new ListTemplatesCommand(templates);

        command.setOut(out);
        command.execute(mock(CommandSession.class));

        out.flush();

        assertThat(outputStream.toString()).contains("jenkins").contains("cdh3");
    }

    private String readDefaultTemplate(String name) {
        try {
            return Resources.toString(Resources.getResource(PoolTemplate.class,
                String.format("/com/axemblr/provisionr/core/templates/%s.xml", name)), Charsets.UTF_8);

        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}

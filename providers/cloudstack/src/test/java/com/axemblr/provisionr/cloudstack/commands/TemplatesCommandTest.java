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

package com.axemblr.provisionr.cloudstack.commands;

import com.google.common.collect.Sets;
import java.util.Set;
import static org.fest.assertions.api.Assertions.assertThat;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.features.TemplateClient;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TemplatesCommandTest extends CommandTestSupport {

    final Set<Template> templates = Sets.newHashSet(Template.builder()
        .id("template-1")
        .name("template-one")
        .build());

    @Test
    public void testTemplatesCommandPrintsTheTemplates() throws Exception {
        final TemplatesCommand templatesCommand = new TemplatesCommand(defaultProviderConfig);
        final TemplateClient templateClient = mock(TemplateClient.class);

        when(client.getTemplateClient()).thenReturn(templateClient);
        when(templateClient.listTemplates()).thenReturn(templates);

        templatesCommand.doExecuteWithContext(client, out);
        out.close();
        assertThat(byteArrayOutputStream.toString()).contains("template-1").contains("template-one");
    }
}

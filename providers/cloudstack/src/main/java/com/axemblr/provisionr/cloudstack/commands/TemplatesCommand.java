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

import com.axemblr.provisionr.cloudstack.DefaultProviderConfig;
import java.io.PrintStream;
import org.apache.felix.gogo.commands.Command;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.Template;

@Command(scope = CommandSupport.CLOUDSTACK_SCOPE, name = TemplatesCommand.NAME,
    description = "Commands to list CloudStack templates")
public class TemplatesCommand extends CommandSupport {

    public static final String NAME = "templates";

    public TemplatesCommand(DefaultProviderConfig providerConfig) {
        super(providerConfig);
    }

    @Override
    public Object doExecuteWithContext(CloudStackClient client, PrintStream out) throws Exception {
        out.printf("CloudStack templates for provider %s\n", getProvider().getId());

        for (Template template : client.getTemplateClient().listTemplates()) {
            out.printf("---\n%s\n", template.toString());
        }
        out.println();
        return null;
    }
}

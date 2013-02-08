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
import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.PrintStream;
import java.util.List;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * List pre-configured templates that can be used to create clusters on different providers
 */
@Command(scope = "provisionr", name = "templates", description = "List of pre-configured templates")
public class ListTemplatesCommand extends OsgiCommandSupport {

    private PrintStream out = System.out;

    private final List<PoolTemplate> templates;

    public ListTemplatesCommand(List<PoolTemplate> templates) {
        this.templates = checkNotNull(templates, "templates is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        for (PoolTemplate template : templates) {
            out.printf("- %s\n\t%s\n", template.getId(), template.getDescription());
        }

        return null;
    }

    @VisibleForTesting
    void setOut(PrintStream out) {
        this.out = checkNotNull(out, "out is null");
    }
}

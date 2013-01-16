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

package org.activiti.karaf.commands;

import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkNotNull;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

@Command(scope = "activiti", name = "add-group", description = "Create new user group")
public class AddGroupCommand extends ActivitiCommand {

    @Option(name = "-i", aliases = "--id", description = "Group ID", required = true)
    private String id;

    @Option(name = "-n", aliases = "--name", description = "Group Name", required = true)
    private String name;

    @Option(name = "-t", aliases = "--type", description = "Group Type")
    private String type = "security-role";

    @Override
    protected Object doExecute() throws Exception {
        IdentityService identityService = getProcessEngine().getIdentityService();

        Group group = identityService.newGroup(id);
        group.setName(name);
        group.setType(type);
        identityService.saveGroup(group);

        return null;
    }

    @VisibleForTesting
    void setId(String id) {
        this.id = checkNotNull(id, "id is null");
    }

    @VisibleForTesting
    void setName(String name) {
        this.name = checkNotNull(name, "name is null");
    }

    @VisibleForTesting
    void setType(String type) {
        this.type = checkNotNull(type, "type is null");
    }
}

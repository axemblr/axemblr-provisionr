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

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Rundeck Resource Model Project definition
 *
 * @see <a href="http://rundeck.org/docs/manpages/man5/resource-v13.html" />
 */
@XmlRootElement(name = "project")
public class Project {

    private List<Node> nodes = Lists.newArrayList();

    public Project() {
    }

    public Project(List<Node> nodes) {
        this.nodes = checkNotNull(nodes, "nodes is null");
    }

    @XmlElement(name = "node")
    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = checkNotNull(nodes, "nodes is null");
    }

    public void addNodes(Node... nodes) {
        Collections.addAll(this.nodes, nodes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        return !(nodes != null ? !nodes.equals(project.nodes) : project.nodes != null);

    }

    @Override
    public int hashCode() {
        return nodes != null ? nodes.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Project{" +
            "nodes=" + nodes +
            '}';
    }
}

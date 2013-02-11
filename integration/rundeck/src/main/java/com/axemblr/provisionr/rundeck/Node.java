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

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * A Rundeck Resource Model is nothing more than just a list of nodes
 * with a predefined set of properties and an optional list of user defined attributes
 *
 * @see <a href="http://rundeck.org/docs/manpages/man5/resource-v13.html#node" />
 */
public class Node {

    /**
     * The node name. This is a logical identifier from the node. (required)
     */
    private String name;

    /**
     * The hostname or IP address of the remote host. (required)
     */
    private String hostname;

    /**
     * Comma separated list of filtering tags. (optional)
     */
    private String tags;

    /**
     * The username used for the remote connection. (required)
     */
    private String username;

    /**
     * User defined attributes
     */
    private List<Attribute> attributes;

    public Node() {
    }

    public Node(String name, String hostname, String username) {
        this.name = checkNotNull(name, "name is null");
        this.hostname = checkNotNull(hostname, "hostname is null");
        this.username = checkNotNull(username, "username is null");
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = checkNotNull(name, "name is null");
    }

    @XmlAttribute
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = checkNotNull(hostname, "hostname is null");
    }

    @XmlAttribute
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = checkNotNull(tags, "tags is null");
    }

    public void setTags(Iterable<String> tags) {
        setTags(Joiner.on(",").join(tags));
    }

    public void setTags(String[] tags) {
        setTags(Joiner.on(",").join(tags));
    }

    @XmlAttribute
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = checkNotNull(username, "username is null");
    }

    @XmlElement(name = "attribute")
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = checkNotNull(attributes, "attributes is null");
    }

    public void setAttributes(Map<String, String> values) {
        this.attributes = Lists.newArrayList();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            this.attributes.add(new Attribute(entry.getKey(), entry.getValue()));
        }
    }

    public void addAttribute(String name, String value) {
        this.attributes.add(new Attribute(name, value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (attributes != null ? !attributes.equals(node.attributes) : node.attributes != null) return false;
        if (hostname != null ? !hostname.equals(node.hostname) : node.hostname != null) return false;
        if (name != null ? !name.equals(node.name) : node.name != null) return false;
        if (tags != null ? !tags.equals(node.tags) : node.tags != null) return false;
        if (username != null ? !username.equals(node.username) : node.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Node{" +
            "name='" + name + '\'' +
            ", hostname='" + hostname + '\'' +
            ", tags='" + tags + '\'' +
            ", username='" + username + '\'' +
            ", attributes=" + attributes +
            '}';
    }
}

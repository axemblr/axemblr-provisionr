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
import javax.xml.bind.annotation.XmlAttribute;

/**
 * An attribute allows you to add user-defined metadata
 *
 * @see <a href="http://rundeck.org/docs/manpages/man5/resource-v13.html#attribute" />
 */
public class Attribute {

    private String name;

    private String value;

    public Attribute() {
    }

    public Attribute(String name, String value) {
        this.name = checkNotNull(name, "name is null");
        this.value = checkNotNull(value, "value is null");
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = checkNotNull(name, "name is null");
    }

    @XmlAttribute(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = checkNotNull(value, "value is null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        if (name != null ? !name.equals(attribute.name) : attribute.name != null) return false;
        if (value != null ? !value.equals(attribute.value) : attribute.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Attribute{" +
            "name='" + name + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}

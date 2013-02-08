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

package com.axemblr.provisionr.core.templates.xml;

import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Represents a custom repository entry from a pool template
 * <p/>
 * Designed to be consumed only by JAXB. It looks like this in xml:
 * <p/>
 * <repository id="cloudera-cdh3">
 * <entries>
 * <entry>deb http://archive.cloudera.com/debian lucid-cdh3 contrib</entry>
 * </entries>
 * <key><![CDATA[-----BEGIN PGP PUBLIC KEY BLOCK----- ]]</key>
 * </repository>
 */
public class RepositoryEntry {

    private String id;

    private List<String> entries = newArrayList();

    private String key;

    public RepositoryEntry() {
    }

    @VisibleForTesting
    RepositoryEntry(String id, List<String> entries, String key) {
        this.id = checkNotNull(id, "id is null");
        this.entries = checkNotNull(entries, "entries is null");
        this.key = checkNotNull(key, "key is null");
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = checkNotNull(id, "id is null");
    }

    @XmlElementWrapper(name = "entries")
    @XmlElement(name = "entry")
    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = checkNotNull(entries, "entries is null");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = checkNotNull(key, "key is null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RepositoryEntry that = (RepositoryEntry) o;

        if (entries != null ? !entries.equals(that.entries) : that.entries != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (entries != null ? entries.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RepositoryEntry{" +
            "id='" + id + '\'' +
            ", entries=" + entries +
            ", key='" + key + '\'' +
            '}';
    }
}

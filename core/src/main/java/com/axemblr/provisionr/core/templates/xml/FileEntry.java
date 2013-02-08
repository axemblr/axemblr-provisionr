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
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents a file entry from a pool template
 * <p/>
 * Designed to be consumed only by JAXB. It looks like this in xml:
 * <p/>
 * <file source="http://archive.cloudera.com/cm4/installer/latest/cloudera-manager-installer.bin"
 * destination="/opt/cloudera-manager-installer.bin"/>
 */
public class FileEntry {

    private String source;
    private String destination;

    public FileEntry() {
    }

    @VisibleForTesting
    FileEntry(String source, String destination) {
        this.source = checkNotNull(source, "source is null");
        this.destination = checkNotNull(destination, "destination is null");
    }

    @XmlAttribute(name = "source")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = checkNotNull(source, "source is null");
    }

    @XmlAttribute(name = "destination")
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = checkNotNull(destination, "destination is null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntry fileEntry = (FileEntry) o;

        if (destination != null ? !destination.equals(fileEntry.destination) : fileEntry.destination != null)
            return false;
        if (source != null ? !source.equals(fileEntry.source) : fileEntry.source != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FileEntry{" +
            "source='" + source + '\'' +
            ", destination='" + destination + '\'' +
            '}';
    }
}

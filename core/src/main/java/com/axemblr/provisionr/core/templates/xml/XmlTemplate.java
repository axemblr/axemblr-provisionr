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

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.NetworkBuilder;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.pool.PoolBuilder;
import com.axemblr.provisionr.api.software.Repository;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.api.software.SoftwareBuilder;
import com.axemblr.provisionr.core.templates.PoolTemplate;
import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Throwables;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Basic representation of a pool template
 * <p/>
 * Designed to be consumed *only* by JAXB
 * <p/>
 * If you need to implement a pool template see {@code PoolTemplate}
 */
@XmlRootElement(name = "template")
public class XmlTemplate implements PoolTemplate {

    /**
     * @return an XmlTemplate instance resulted from parsing the content
     */
    public static XmlTemplate newXmlTemplate(String content) {
        try {
            JAXBContext context = JAXBContext.newInstance(XmlTemplate.class);
            return (XmlTemplate) context.createUnmarshaller()
                .unmarshal(new ByteArrayInputStream(content.getBytes()));

        } catch (JAXBException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * @return an XmlTemplate instance resulted from parsing a file
     */
    public static XmlTemplate newXmlTemplate(File file) {
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            return newXmlTemplate(CharStreams.toString(reader));

        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            Closeables.closeQuietly(reader);
        }
    }

    private String id;
    private String description;
    private String osVersion;

    private List<String> packages = newArrayList();
    private List<Integer> ports = newArrayList();

    private List<FileEntry> files = newArrayList();

    private List<RepositoryEntry> repositories = newArrayList();

    public XmlTemplate() {
    }

    @Override
    public Pool apply(Pool pool) {
        PoolBuilder result = pool.toBuilder();

        result.software(apply(pool.getSoftware()));
        result.network(apply(pool.getNetwork()));

        if (osVersion != null) {
            result.provider(pool.getProvider().toBuilder()
                .option("version", osVersion).createProvider());
        }

        return result.createPool();
    }

    @VisibleForTesting
    Software apply(Software software) {
        SoftwareBuilder result = software.toBuilder();

        // Add all the new packages
        for (String pkg : packages) {
            result.addPackage(pkg);
        }

        // Add all the new files
        for (FileEntry entry : files) {
            result.file(entry.getSource(), entry.getDestination());
        }

        // Add all the new custom repositories
        for (RepositoryEntry entry : repositories) {
            result.repository(Repository.builder().name(entry.getId()).key(entry.getKey())
                .entries(entry.getEntries()).createRepository());
        }

        return result.createSoftware();
    }

    @VisibleForTesting
    Network apply(Network network) {
        NetworkBuilder result = network.toBuilder();
        for (int port : ports) {
            result.addRules(Rule.builder().anySource().tcp().port(port).createRule());
        }
        return result.createNetwork();
    }

    @XmlAttribute(name = "id")
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = checkNotNull(id, "id is null");
    }

    @Override
    public String getDescription() {
        return description.trim();
    }

    public void setDescription(String description) {
        this.description = checkNotNull(description, "description is null");
    }

    @XmlAttribute(name = "os-version")
    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = checkNotNull(osVersion, "osVersion is null");
    }

    @XmlElementWrapper(name = "packages")
    @XmlElement(name = "package")
    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = checkNotNull(packages, "packages is null");
    }

    @XmlElementWrapper(name = "ports")
    @XmlElement(name = "port")
    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = checkNotNull(ports, "ports is null");
    }

    @XmlElementWrapper(name = "files")
    @XmlElement(name = "file")
    public List<FileEntry> getFiles() {
        return files;
    }

    public void setFiles(List<FileEntry> files) {
        this.files = checkNotNull(files, "files is null");
    }

    @XmlElementWrapper(name = "repositories")
    @XmlElement(name = "repository")
    public List<RepositoryEntry> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RepositoryEntry> repositories) {
        this.repositories = checkNotNull(repositories, "repositories is null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XmlTemplate that = (XmlTemplate) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (files != null ? !files.equals(that.files) : that.files != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (osVersion != null ? !osVersion.equals(that.osVersion) : that.osVersion != null) return false;
        if (packages != null ? !packages.equals(that.packages) : that.packages != null) return false;
        if (ports != null ? !ports.equals(that.ports) : that.ports != null) return false;
        if (repositories != null ? !repositories.equals(that.repositories) : that.repositories != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (osVersion != null ? osVersion.hashCode() : 0);
        result = 31 * result + (packages != null ? packages.hashCode() : 0);
        result = 31 * result + (ports != null ? ports.hashCode() : 0);
        result = 31 * result + (files != null ? files.hashCode() : 0);
        result = 31 * result + (repositories != null ? repositories.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "XmlTemplate{" +
            "id='" + id + '\'' +
            ", description='" + description + '\'' +
            ", osVersion='" + osVersion + '\'' +
            ", packages=" + packages +
            ", ports=" + ports +
            ", files=" + files +
            ", repositories=" + repositories +
            '}';
    }
}

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

package com.axemblr.provisionr.core.templates;

import com.axemblr.provisionr.core.templates.xml.XmlTemplate;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.concurrent.ConcurrentMap;
import org.apache.felix.fileinstall.ArtifactInstaller;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener for pool template files
 * <p/>
 * When a new xml file is places under templates this class is
 * notified and a new pool template is registered as a service
 */
public class PoolTemplateInstaller implements ArtifactInstaller {

    private static final Logger LOG = LoggerFactory.getLogger(PoolTemplateInstaller.class);

    public static final String TEMPLATES_FOLDER = "templates";
    public static final String TEMPLATES_EXTENSION = ".xml";

    /**
     * A bundle context provided by the OSGi framework
     */
    private final BundleContext bundleContext;

    /**
     * A map with all the pool templates registered by this listener
     */
    private final ConcurrentMap<String, ServiceRegistration> templates;

    public PoolTemplateInstaller(BundleContext bundleContext) {
        this.bundleContext = checkNotNull(bundleContext, "bundleContext is null");
        this.templates = Maps.newConcurrentMap();
    }

    @Override
    public boolean canHandle(File file) {
        return TEMPLATES_FOLDER.equals(file.getParentFile().getName())
            && file.getName().endsWith(TEMPLATES_EXTENSION);
    }

    /**
     * Install a new pool template as a service using the file content
     * <p/>
     * The absolute file path is the unique identifier
     */
    @Override
    public void install(File file) throws Exception {
        final String absolutePath = file.getAbsolutePath();
        LOG.info("Installing Pool template from  " + absolutePath);

        if (!templates.containsKey(absolutePath)) {
            PoolTemplate template = XmlTemplate.newXmlTemplate(file);
            ServiceRegistration registration = bundleContext
                .registerService(PoolTemplate.class.getName(), template, null);

            templates.put(absolutePath, registration);
            LOG.info("Registered new template with ID: " + template.getId());
        }
    }

    /**
     * Uninstall a pool description identified by the absolute file path
     */
    @Override
    public void uninstall(File file) throws Exception {
        final String absolutePath = file.getAbsolutePath();
        LOG.info("Uninstalling Pool template for path " + absolutePath);

        if (templates.containsKey(absolutePath)) {
            templates.remove(absolutePath).unregister();
        }
    }

    /**
     * Update a pool template
     * <p/>
     * This method performs no actions if there is no pool registered for this file
     */
    @Override
    public void update(File file) throws Exception {
        if (templates.containsKey(file.getAbsolutePath())) {
            uninstall(file);
            install(file);
        }
    }
}

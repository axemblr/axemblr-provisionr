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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Dictionary;
import org.apache.ibatis.io.Resources;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class PoolTemplateInstallerTest {

    @Test
    public void testInstallAndUninstallTemplate() throws Exception {
        BundleContext bundleContext = mock(BundleContext.class);

        ServiceRegistration registration = mock(ServiceRegistration.class);
        when(bundleContext.registerService(eq(PoolTemplate.class.getName()), any(),
            Matchers.<Dictionary<String, ?>>any())).thenReturn(registration);

        File file = getPathToCdh3Template();
        PoolTemplateInstaller installer = new PoolTemplateInstaller(bundleContext);

        installer.install(file);
        verify(bundleContext).registerService(eq(PoolTemplate.class.getName()),
            any(), (Dictionary<String, ?>) isNull());
        verifyZeroInteractions(registration);

        installer.uninstall(file);
        verify(registration).unregister();
    }

    private File getPathToCdh3Template() throws URISyntaxException, IOException {
        return new File(Resources.getResourceURL("com/axemblr/provisionr/core/templates/cdh3.xml").toURI());
    }
}

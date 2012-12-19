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

package com.axemblr.provisionr.cloudstack.commands;

import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.cloudstack.DefaultProviderConfig;
import com.google.common.base.Optional;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.jclouds.cloudstack.CloudStackClient;
import org.junit.Before;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Support class for CloudStack Karaf Command tests.
 */
public class CommandTestSupport {

    protected DefaultProviderConfig defaultProviderConfig;
    protected ByteArrayOutputStream byteArrayOutputStream;
    protected PrintStream out;
    protected CloudStackClient client;

    @Before
    public void setUp() throws Exception {
        defaultProviderConfig = mock(DefaultProviderConfig.class);
        byteArrayOutputStream = new ByteArrayOutputStream();
        out = new PrintStream(byteArrayOutputStream);
        client = mock(CloudStackClient.class);
        when(defaultProviderConfig.createProvider()).thenReturn(Optional.of(mock(Provider.class)));
    }
}

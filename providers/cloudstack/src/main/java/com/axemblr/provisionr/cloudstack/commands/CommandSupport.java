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
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import java.io.PrintStream;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.jclouds.ContextBuilder;
import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.RestContext;


/**
 * Base class for CloudStack  Karaf Shell commands. It takes care of creating and cleaning a
 * {@link org.jclouds.cloudstack.CloudStackContext} for each command.
 */
public abstract class CommandSupport extends OsgiCommandSupport {

    private RestContext<CloudStackClient, CloudStackAsyncClient> context = null;
    public static final String CLOUDSTACK_SCOPE = "cloudstack";

    private final Provider provider;

    protected CommandSupport(DefaultProviderConfig providerConfig) {
        this.provider = providerConfig.createProvider().get();
    }

    public abstract Object doExecuteWithContext(CloudStackClient client, PrintStream out) throws Exception;

    @Override
    protected Object doExecute() throws Exception {
        try {
            context = newCloudStackContext(provider);
            return doExecuteWithContext(context.getApi(), getOut());
        } finally {
            Closeables.closeQuietly(context);
        }
    }

    protected RestContext<CloudStackClient, CloudStackAsyncClient> newCloudStackContext(Provider provider) {
        return ContextBuilder.newBuilder(new CloudStackApiMetadata())
            .endpoint(provider.getEndpoint().get())
            .modules(ImmutableSet.of(new SLF4JLoggingModule()))
            .credentials(provider.getAccessKey(), provider.getSecretKey())
            .build(CloudStackApiMetadata.CONTEXT_TOKEN);
    }

    public Provider getProvider() {
        return provider;
    }

    /**
     * Convenience method for easy unit testing of Commands.
     */
    public PrintStream getOut() {
        return System.out;
    }
}

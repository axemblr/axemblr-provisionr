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

package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.commands.predicates.ProvisionrPredicates;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * A typical call looks like this:
 * <p/>
 * $ provisionr:create --id amazon --key web-1 --size 5 --hardware-type m1.small \
 * --port 80 --port 443 --package nginx --package gunicorn --package python-pip
 */
@Command(scope = "provisionr", name = "create", description = "Create pool of virtual machines")
public class CreatePoolCommand extends OsgiCommandSupport {

    @Option(name = "--id", description = "Service ID (use provisionr:services)", required = true)
    private String id;

    @Option(name = "-k", aliases = "--key", description = "Unique business identifier for this pool", required = true)
    private String key;

    @Option(name = "-s", aliases = "--size", description = "Expected pool size")
    private int size = 1;

    @Option(name = "-h", aliases = "--hardware-type", description = "Virtual machine hardware type")
    private String hardwareType = "t1.micro";

    @Option(name = "--port", description = "Firewall port that need to be open for any TCP traffic " +
        "(multi-valued). SSH (22) is always open by default.", multiValued = true)
    private List<Integer> ports = Lists.newArrayList();

    @Option(name = "--package", description = "Package to install by default (multi-valued)",
        multiValued = true)
    private List<String> packages = Lists.newArrayList();

    @Option(name = "--cache", description = "Cache base operating system image (including files & packages)")
    private boolean cacheBaseImage = false;

    private final List<Provisionr> services;

    public CreatePoolCommand(List<Provisionr> services) {
        this.services = checkNotNull(services, "services is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        checkArgument(size > 0, "size should be a positive integer");

        Optional<Provisionr> service = Iterables.tryFind(services, ProvisionrPredicates.withId(id));
        if (service.isPresent()) {
            final Pool pool = createPoolFromArgumentsAndServiceDefaults(service.get());

            final String processInstanceId = service.get().startPoolManagementProcess(key, pool);
            return String.format("Pool management process started (id: %s)", processInstanceId);
        } else {
            throw new NoSuchElementException("No provisioning service found with id: " + id);
        }
    }

    protected Pool createPoolFromArgumentsAndServiceDefaults(Provisionr service) {
        final Optional<Provider> defaultProvider = service.getDefaultProvider();
        checkArgument(defaultProvider.isPresent(), String.format("please configure a default provider " +
            "by editing etc/com.axemblr.provisionr.%s.cfg", id));

        /* Always allow ICMP and ssh traffic by default */
        final Network network = Network.builder().addRules(
            Rule.builder().anySource().icmp().createRule(),
            Rule.builder().anySource().tcp().port(22).createRule()
        ).addRules(
            formatPortsAsIngressRules()
        ).createNetwork();

        final Hardware hardware = Hardware.builder().type(hardwareType).createHardware();

        final Software software = Software.builder().packages(packages).createSoftware();

        return Pool.builder()
            .provider(defaultProvider.get())
            .hardware(hardware)
            .software(software)
            .network(network)
            .adminAccess(collectCurrentUserCredentialsForAdminAccess())
            .minSize(size)
            .expectedSize(size)
            .cacheBaseImage(cacheBaseImage)
            .createPool();
    }

    private Set<Rule> formatPortsAsIngressRules() {
        ImmutableSet.Builder<Rule> rules = ImmutableSet.builder();
        for (int port : ports) {
            rules.add(Rule.builder().anySource().tcp().port(port).createRule());
        }
        return rules.build();
    }

    private AdminAccess collectCurrentUserCredentialsForAdminAccess() {
        String userHome = System.getProperty("user.home");

        try {
            String publicKey = Files.toString(new File(userHome, ".ssh/id_rsa.pub"), Charsets.UTF_8);
            String privateKey = Files.toString(new File(userHome, ".ssh/id_rsa"), Charsets.UTF_8);

            return AdminAccess.builder().username(System.getProperty("user.name"))
                .publicKey(publicKey).privateKey(privateKey).createAdminAccess();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @VisibleForTesting
    void setId(String id) {
        this.id = checkNotNull(id, "id is null");
    }

    @VisibleForTesting
    void setKey(String key) {
        this.key = checkNotNull(key, "key is null");
    }

    @VisibleForTesting
    void setSize(int size) {
        checkArgument(size > 0, "size should be a positive number");
        this.size = size;
    }

    @VisibleForTesting
    void setHardwareType(String hardwareType) {
        this.hardwareType = checkNotNull(hardwareType, "hardwareType is null");
    }

    @VisibleForTesting
    void setPorts(List<Integer> ports) {
        this.ports = ImmutableList.copyOf(ports);
    }

    @VisibleForTesting
    void setPackages(List<String> packages) {
        this.packages = ImmutableList.copyOf(packages);
    }

    @VisibleForTesting
    void setCacheBaseImage(boolean cacheBaseImage) {
        this.cacheBaseImage = cacheBaseImage;
    }
}

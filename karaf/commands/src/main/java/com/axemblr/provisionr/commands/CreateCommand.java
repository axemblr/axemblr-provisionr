package com.axemblr.provisionr.commands;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.commands.predicates.ProvisionrPredicates;
import com.axemblr.provisionr.core.templates.PoolTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Service;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

public abstract class CreateCommand extends OsgiCommandSupport {

    @Option(name = "--id", description = "Service ID (use provisionr:services)", required = true)
    protected String id;

    @Option(name = "--package", description = "Package to install by default (multi-valued)",
            multiValued = true)
    protected List<String> packages = Lists.newArrayList();

    @Option(name = "-t", aliases = "--template", description = "Pre-configured template (packages, files)")
    protected String template;

    @Option(name = "--timeout", description = "Timeout in seconds for the command's initialization steps. " +
            "If not specified, defaults to 600 seconds.")
    protected int bootstrapTimeout = 600;

    protected final List<Provisionr> services;

    protected final List<PoolTemplate> templates;

    public CreateCommand(List<Provisionr> services, List<PoolTemplate> templates) {
        this.services = checkNotNull(services, "services is null");
        this.templates = checkNotNull(templates, "templates is null");
    }

    @VisibleForTesting
    void setId(String id) {
        this.id = checkNotNull(id, "id is null");
    }

    @VisibleForTesting
    void setPackages(List<String> packages) {
        this.packages = ImmutableList.copyOf(packages);
    }

    @VisibleForTesting
    void setTemplate(String template) {
        this.template = checkNotNull(template, "template is null");
    }

    @VisibleForTesting
    AdminAccess collectCurrentUserCredentialsForAdminAccess() {
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
    Pool applyTemplate(Pool pool) {
        for (PoolTemplate candidate : templates) {
            if (candidate.getId().equalsIgnoreCase(template)) {
                return candidate.apply(pool);
            }
        }
        throw new NoSuchElementException("No pool template found with name: " + template);
    }

    @VisibleForTesting
    Network buildNetwork(List<Integer> ports) {
        /* Always allow ICMP and ssh traffic by default */
        return Network.builder().addRules(
            Rule.builder().anySource().icmp().createRule(),
            Rule.builder().anySource().tcp().port(22).createRule()
        ).addRules(
            formatPortsAsIngressRules(ports)
        ).createNetwork();
    }

    Optional<Provider> getDefaultProvider(Provisionr service) {
        checkArgument(service.getDefaultProvider().isPresent(), String.format("please configure a default provider " +
            "by editing etc/com.axemblr.provisionr.%s.cfg", id));
        return service.getDefaultProvider();
    }

    Provisionr getService() {
        Optional<Provisionr> service = Iterables.tryFind(services, ProvisionrPredicates.withId(id));
        if (!service.isPresent()) {
            throw new NoSuchElementException("No provisioning service found with id: " + id);
        }
        return service.get();
    }

    private Set<Rule> formatPortsAsIngressRules(List<Integer> ports) {
        ImmutableSet.Builder<Rule> rules = ImmutableSet.builder();
        for (int port : ports) {
            rules.add(Rule.builder().anySource().tcp().port(port).createRule());
        }
        return rules.build();
    }
}

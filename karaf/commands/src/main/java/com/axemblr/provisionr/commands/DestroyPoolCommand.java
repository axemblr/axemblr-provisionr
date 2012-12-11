package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.commands.functions.ProvisionrPredicates;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import java.util.List;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "provisionr", name = "destroy", description = "Destroy pool")
public class DestroyPoolCommand extends OsgiCommandSupport {

    @Option(name = "--id", description = "Provisioning service ID", required = true)
    private String id;

    @Option(name = "-k", aliases = "--key", description = "Business pool key", required = true)
    private String businessKey;

    private final List<Provisionr> services;

    public DestroyPoolCommand(List<Provisionr> services) {
        this.services = checkNotNull(services, "services is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        Provisionr service = Iterables.find(services, ProvisionrPredicates.withId(id));
        service.destroyPool(businessKey);

        return null;
    }
}

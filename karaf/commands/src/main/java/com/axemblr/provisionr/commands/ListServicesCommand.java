package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.api.Provisionr;
import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import java.io.PrintStream;
import java.util.List;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "provisionr", name = "services", description = "List provisioning services")
public class ListServicesCommand extends OsgiCommandSupport {

    private static final PrintStream out = System.out;

    private final List<Provisionr> services;

    public ListServicesCommand(List<Provisionr> services) {
        this.services = checkNotNull(services, "services is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        List<String> ids = Lists.newArrayList();
        for (Provisionr service : services) {
            ids.add(service.getId());
        }
        out.printf("Services: %s%n", Joiner.on(", ").join(ids));
        return null;
    }
}

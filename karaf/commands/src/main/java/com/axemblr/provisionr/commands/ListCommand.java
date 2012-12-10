package com.axemblr.provisionr.commands;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "provisionr", name = "list", description = "List running pools")
public class ListCommand extends OsgiCommandSupport {

    @Option(name = "-v", aliases = "--verbose", description = "Full details of the pool instance")
    private boolean verbose;

    @Override
    protected Object doExecute() throws Exception {
        System.out.println("Display list of running pools. Verbose: " + verbose);
        return null;
    }
}

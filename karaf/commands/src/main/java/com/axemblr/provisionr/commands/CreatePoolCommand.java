package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.commands.functions.ProvisionrPredicates;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import java.io.File;
import java.util.List;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

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

    @Option(name = "--ports", description = "Firewall ports that need to be open",
        multiValued = true, valueToShowInHelp = "22")
    private int[] ports = new int[]{22};

    @Option(name = "--packages", description = "Packages to install by default",
        multiValued = true, valueToShowInHelp = "git-core,vim")
    private String[] packages = new String[]{"git-core", "vim"};

    @Option(name = "--cache", description = "Cache base operating system image (including files & packages)")
    private boolean cacheBaseImage = false;

    private final List<Provisionr> services;

    public CreatePoolCommand(List<Provisionr> services) {
        this.services = checkNotNull(services, "services is null");
    }

    @Override
    protected Object doExecute() throws Exception {
        checkArgument(size > 0, "size should be a positive integer");

        final Provisionr service = Iterables.find(services, ProvisionrPredicates.withId(id));
        final Pool pool = createPoolFromArgumentsAndServiceDefaults(service);

        final String processInstanceId = service.startPoolManagementProcess(key, pool);
        return String.format("Pool management process started (id: %s)", processInstanceId);
    }

    private Pool createPoolFromArgumentsAndServiceDefaults(Provisionr service) {
        final Optional<Provider> defaultProvider = service.getDefaultProvider();
        checkArgument(defaultProvider.isPresent(), String.format("please configure a default provider " +
            "by editing etc/com.axemblr.provisionr.%s.cfg", id));

        final Network network = Network.builder().addRules(
            Rule.builder().anySource().icmp().createRule(),
            Rule.builder().anySource().tcp().port(22).createRule()
        ).createNetwork();

        final Hardware hardware = Hardware.builder().type(hardwareType).createHardware();

        final Software software = Software.builder().packages(packages).createSoftware();

        return Pool.builder().provider(defaultProvider.get()).hardware(hardware).software(software)
            .network(network).adminAccess(collectCurrentUserCredentialsForAdminAccess())
            .minSize(size).expectedSize(size).cacheBaseImage(cacheBaseImage).createPool();
    }

    protected AdminAccess collectCurrentUserCredentialsForAdminAccess() {
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
}

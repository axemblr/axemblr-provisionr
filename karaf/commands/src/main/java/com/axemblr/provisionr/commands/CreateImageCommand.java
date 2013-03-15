package com.axemblr.provisionr.commands;

import static com.google.common.base.Preconditions.checkNotNull;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.hardware.HardwareBuilder;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.commands.predicates.ProvisionrPredicates;
import com.axemblr.provisionr.core.templates.PoolTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.felix.gogo.commands.Command;

@Command(scope = "provisionr", name = "cache", description = "Create a cached golden image.")
public class CreateImageCommand extends CreateCommand {

    // TODO: remove this and use a provided parameter
    private static final String HARDWARE_TYPE = "t1.micro";

    public CreateImageCommand(List<Provisionr> services, List<PoolTemplate> templates) {
        super(services, templates);
    }

    @Override
    protected Object doExecute() throws Exception {
        Provisionr service = getService();
        final Pool pool = createPoolOfOne(service);
        // TODO: create service.startCachingProcess(uuid, pool) in the Provisionr class
        return null;
    }

    @VisibleForTesting
    Pool createPoolOfOne(Provisionr service) {

        final Software software = Software.builder().packages(packages).createSoftware();
        final Hardware hardware = Hardware.builder().type(HARDWARE_TYPE).createHardware();

        final Pool pool = Pool.builder()
                .provider(getDefaultProvider(service).get())
                .hardware(hardware)
                .software(software)
                .network(buildNetwork(new ArrayList<Integer>()))
                .adminAccess(collectCurrentUserCredentialsForAdminAccess())
                .minSize(1)
                .expectedSize(1)
                .bootstrapTimeInSeconds(bootstrapTimeout)
                .createPool();

        return template != null ? applyTemplate(pool) : pool;

    }

}

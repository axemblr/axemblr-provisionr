package com.axemblr.provisionr.client;

import com.axemblr.provisionr.api.Provisionr;
import java.util.List;
import java.util.UUID;

public class ProvisionrClient {

    private final List<Provisionr> services;

    public ProvisionrClient(List<Provisionr> services) {
        this.services = services;
    }

    public void init() {
        System.out.println("**** Starting client. Got " + services.size() + " services.");
        for (Provisionr service : services) {
            service.startCreatePoolProcess(UUID.randomUUID().toString(), null);
        }
    }

    public List<Provisionr> getServices() {
        return services;
    }
}

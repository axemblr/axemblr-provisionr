package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.core.KeyPairs;
import org.activiti.engine.delegate.DelegateExecution;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.options.DeployVirtualMachineOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunInstances extends CloudStackActivity {

    public static final Logger LOG = LoggerFactory.getLogger(RunInstances.class);
    public static final String ZONE_ID = "cloudstack.provider.zoneId";

    @Override
    public void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution) {
        final String businessKey = execution.getProcessBusinessKey();

        final String keyPairName = KeyPairs.formatNameFromBusinessKey(businessKey);

        final String zoneId = pool.getOptions().get(ZONE_ID);
        final String templateId = pool.getSoftware().getBaseOperatingSystem();
        final String serviceOffering = pool.getHardware().getType();

        LOG.info("Starting instances!");

        AsyncCreateResponse asyncCreateResponse = cloudStackClient.getVirtualMachineClient()
            .deployVirtualMachineInZone(zoneId, serviceOffering, templateId, DeployVirtualMachineOptions.Builder
                .displayName(businessKey)
                .group(businessKey)
                .keyPair(keyPairName)
                .name(businessKey));
    }
}

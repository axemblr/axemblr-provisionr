package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.core.KeyPairs;
import org.activiti.engine.delegate.DelegateExecution;
import org.jclouds.cloudstack.CloudStackClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteKeyPair extends CloudStackActivity {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteKeyPair.class);

    @Override
    public void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution) {
        String keyName = KeyPairs.formatNameFromBusinessKey(execution.getProcessBusinessKey());
        LOG.info("Deleting Admin Access Key pair {}", keyName);
        cloudStackClient.getSSHKeyPairClient().deleteSSHKeyPair(keyName);
    }
}

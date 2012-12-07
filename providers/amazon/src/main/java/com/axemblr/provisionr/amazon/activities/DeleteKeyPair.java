package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.axemblr.provisionr.amazon.KeyPairs;
import com.axemblr.provisionr.api.pool.Pool;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteKeyPair extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteKeyPair.class);

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        String keyName = KeyPairs.formatNameFromBusinessKey(execution.getProcessBusinessKey());

        LOG.info(">> Deleting key pair {}", keyName);
        client.deleteKeyPair(new DeleteKeyPairRequest().withKeyName(keyName));
    }
}

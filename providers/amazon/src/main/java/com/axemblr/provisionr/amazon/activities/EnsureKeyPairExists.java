/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.ImportKeyPairRequest;
import com.amazonaws.services.ec2.model.ImportKeyPairResult;
import com.axemblr.provisionr.amazon.core.ErrorCodes;
import com.axemblr.provisionr.amazon.core.KeyPairs;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.cache.LoadingCache;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureKeyPairExists extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(EnsureKeyPairExists.class);

    public EnsureKeyPairExists(ProviderClientCache cache) {
        super(cache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) {
        String keyName = KeyPairs.formatNameFromBusinessKey(execution.getProcessBusinessKey());
        LOG.info(">> Importing admin access key pair as {}", keyName);

        final String publicKey = pool.getAdminAccess().getPublicKey();
        try {
            importPoolPublicKeyPair(client, keyName, publicKey);

        } catch (AmazonServiceException e) {
            if (e.getErrorCode().equals(ErrorCodes.DUPLICATE_KEYPAIR)) {
                LOG.info("<< Duplicate key pair found. Re-importing from pool description");

                client.deleteKeyPair(new DeleteKeyPairRequest().withKeyName(keyName));
                importPoolPublicKeyPair(client, keyName, publicKey);
            }
        }
    }

    private void importPoolPublicKeyPair(AmazonEC2 client, String keyName, String publicKey) {
        ImportKeyPairResult result = client.importKeyPair(new ImportKeyPairRequest()
            .withKeyName(keyName).withPublicKeyMaterial(publicKey));
        LOG.info("<< Created remote key with fingerprint {}", result.getKeyFingerprint());
    }
}

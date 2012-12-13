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

package com.axemblr.provisionr.cloudstack.activities;

import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.core.KeyPairs;
import org.activiti.engine.delegate.DelegateExecution;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.features.SSHKeyPairClient;
import org.jclouds.crypto.SshKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsureKeyPairExists extends CloudStackActivity {

    private static final Logger LOG = LoggerFactory.getLogger(EnsureKeyPairExists.class);

    @Override
    public void execute(CloudStackClient cloudStackClient, Pool pool, DelegateExecution execution) {
        String keyName = KeyPairs.formatNameFromBusinessKey(execution.getProcessBusinessKey());
        LOG.info("Creating admin access key pair as {}", keyName);
        SSHKeyPairClient sshKeyPairClient = cloudStackClient.getSSHKeyPairClient();
        try {
            SshKeyPair sshKeyPair = sshKeyPairClient.registerSSHKeyPair(keyName, pool.getAdminAccess().getPublicKey());
            LOG.info("Registered remote key with fingerprint {}", sshKeyPair.getFingerprint());
        } catch (IllegalStateException e) {
            LOG.warn("Key with name {} already exists", keyName);
            SshKeyPair key = sshKeyPairClient.getSSHKeyPair(keyName);
            if (key.getFingerprint().equals(SshKeys.fingerprintPublicKey(pool.getAdminAccess().getPublicKey()))) {
                LOG.info("Fingerprints match. Not updating admin access key pair.");
            } else {
                LOG.info("Fingerprint do not match. Replacing admin access key pair.");
                sshKeyPairClient.deleteSSHKeyPair(keyName);
                sshKeyPairClient.registerSSHKeyPair(keyName, pool.getAdminAccess().getPublicKey());
            }
        }
    }
}

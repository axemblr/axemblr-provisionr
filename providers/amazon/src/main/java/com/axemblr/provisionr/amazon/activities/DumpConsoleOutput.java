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

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.GetConsoleOutputRequest;
import com.amazonaws.services.ec2.model.GetConsoleOutputResult;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.axemblr.provisionr.api.pool.Machine;
import com.axemblr.provisionr.api.pool.Pool;
import com.google.common.base.Charsets;
import static com.google.common.base.Preconditions.checkNotNull;
import net.schmizz.sshj.common.Base64;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DumpConsoleOutput extends AmazonActivity {

    private static final Logger LOG = LoggerFactory.getLogger(DumpConsoleOutput.class);

    public DumpConsoleOutput(ProviderClientCache providerClientCache) {
        super(providerClientCache);
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        Machine machine = (Machine) execution.getVariable("machine");
        checkNotNull(machine, "expecting 'machine' as a process variable");

        LOG.info(">> Requesting console output for instance {}", machine.getExternalId());
        GetConsoleOutputResult result = client.getConsoleOutput(
            new GetConsoleOutputRequest().withInstanceId(machine.getExternalId()));
        if (result.getOutput() != null) {
            String content = new String(Base64.decode(result.getOutput()), Charsets.UTF_8);
            LOG.info("<< Console output for instance {}: {}", machine.getExternalId(), content);
        } else {
            LOG.warn("<< Console output was null for instance {}", machine.getExternalId());
        }
    }
}

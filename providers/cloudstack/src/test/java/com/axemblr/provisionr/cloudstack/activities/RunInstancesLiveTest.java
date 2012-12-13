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

import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.software.Software;
import com.axemblr.provisionr.cloudstack.ProcessVariables;
import com.axemblr.provisionr.cloudstack.core.VirtualMachines;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunInstancesLiveTest extends CloudStackActivityLiveTest<RunInstances> {

    private static final Logger LOG = LoggerFactory.getLogger(RunInstancesLiveTest.class);

    private DelegateExecution execution;
    private Pool pool;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        logSecurityGroupDetails();
        logKeyPairs();
        logVirtualMachines();
        execution = mock(DelegateExecution.class);
        pool = mock(Pool.class);

        final AdminAccess adminAccess = AdminAccess.builder()
            .username("admin")
            .publicKey(getResourceAsString("keys/test.pub"))
            .privateKey(getResourceAsString("keys/test"))
            .createAdminAccess();

        final Network network = Network.builder().addRules(
            Rule.builder().anySource().tcp().port(22).createRule()).createNetwork();

        final Hardware hardware = Hardware.builder().type(getProviderProperty("serviceOffering")).createHardware();
        final Software software = Software.builder()
            .baseOperatingSystem(getProviderProperty("templateId"))
            .createSoftware();

        Map<String, String> options = ImmutableMap.of(RunInstances.ZONE_ID,
            getProviderProperty("zoneId"));

        when(pool.getProvider()).thenReturn(provider);
        when(pool.getAdminAccess()).thenReturn(adminAccess);
        when(pool.getNetwork()).thenReturn(network);
        when(pool.getHardware()).thenReturn(hardware);
        when(pool.getSoftware()).thenReturn(software);
        when(pool.getOptions()).thenReturn(options);

        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);
        when(execution.getVariable(ProcessVariables.POOL)).thenReturn(pool);

        new EnsureSecurityGroupExists().execute(execution);
        new EnsureKeyPairExists().execute(execution);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        new DeleteKeyPair().execute(execution);
        new DeleteSecurityGroup().execute(execution);

        logSecurityGroupDetails();
        logKeyPairs();
        logVirtualMachines();
        VirtualMachines.destroyAllVirtualMachineByName(context.getApi(), BUSINESS_KEY);
        super.tearDown();
    }

    @Test
    public void test() throws Exception {
        activity.execute(execution);
    }
}

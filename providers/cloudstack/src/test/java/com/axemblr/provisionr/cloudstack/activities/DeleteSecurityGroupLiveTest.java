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

import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.cloudstack.ProcessVariables;
import com.axemblr.provisionr.cloudstack.core.SecurityGroups;
import java.util.NoSuchElementException;
import org.activiti.engine.delegate.DelegateExecution;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteSecurityGroupLiveTest extends CloudStackActivityLiveTest<DeleteSecurityGroup> {

    private final Logger LOG = LoggerFactory.getLogger(DeleteSecurityGroupLiveTest.class);
    private final String SECURITY_GROUP_NAME = SecurityGroups.formatNameFromBusinessKey(BUSINESS_KEY);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        logSecurityGroupDetails();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        logSecurityGroupDetails();
        super.tearDown();
    }

    @Test
    public void testDeleteSecurityGroup() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);
        final Network network = Network.builder().createNetwork();

        Pool pool = mock(Pool.class);

        when(pool.getProvider()).thenReturn(provider);
        when(pool.getNetwork()).thenReturn(network);

        when(execution.getVariable(ProcessVariables.POOL)).thenReturn(pool);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);

        SecurityGroups.createSecurityGroup(context.getApi(), SECURITY_GROUP_NAME);

        activity.execute(execution);

        try {
            SecurityGroups.getByName(context.getApi(), SECURITY_GROUP_NAME);
            fail("Does not throw Exception as it should have");
        } catch (NoSuchElementException e) {
            LOG.info("Exception thrown. Test passed.");
        }
    }
}

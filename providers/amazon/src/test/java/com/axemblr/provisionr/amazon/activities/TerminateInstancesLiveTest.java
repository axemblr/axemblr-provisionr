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

import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.api.access.AdminAccess;
import com.axemblr.provisionr.api.hardware.Hardware;
import com.axemblr.provisionr.api.network.Network;
import com.axemblr.provisionr.api.network.Rule;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.axemblr.provisionr.test.ProcessVariablesCollector;
import org.activiti.engine.delegate.DelegateExecution;
import org.junit.Test;
import org.mockito.Matchers;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TerminateInstancesLiveTest extends CreatePoolLiveTest<TerminateInstances> {

    private ProcessVariablesCollector collector;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        collector = new ProcessVariablesCollector();
        collector.install(execution);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTerminateInstances() throws Exception {
        executeActivitiesInSequence(execution,
                EnsureKeyPairExists.class,
                EnsureSecurityGroupExists.class,
                RunOnDemandInstances.class
            );

        when(execution.getVariable(ProcessVariables.INSTANCE_IDS))
            .thenReturn(collector.getVariable(ProcessVariables.INSTANCE_IDS));

        activity.execute(execution);

        /* the second execution should do nothing */
        activity.execute(execution);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTerminateInstancesNoIdsPresent() throws Exception {
        executeActivitiesInSequence(execution,
                EnsureKeyPairExists.class,
                EnsureSecurityGroupExists.class
            );
        when(execution.getVariable(ProcessVariables.INSTANCE_IDS)).thenReturn(null);

        // should not throw any exception and should not do anything
        activity.execute(execution);
    }
}

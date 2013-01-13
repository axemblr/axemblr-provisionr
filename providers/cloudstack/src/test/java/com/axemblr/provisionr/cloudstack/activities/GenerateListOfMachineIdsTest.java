/*
 * Copyright (c) 2013 S.C. Axemblr Software Solutions S.R.L
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
import com.axemblr.provisionr.cloudstack.ProcessVariables;
import com.axemblr.provisionr.core.CoreProcessVariables;
import com.axemblr.provisionr.test.ProcessVariablesCollector;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.activiti.engine.delegate.DelegateExecution;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenerateListOfMachineIdsTest {

    private static final String BUSINESS_KEY = UUID.randomUUID().toString();
    private static final int EXPECTED_SIZE = (new Random()).nextInt(1000);

    private DelegateExecution execution;
    private Pool pool;
    private ProcessVariablesCollector collector;

    @Before
    public void setUp() throws Exception {
        execution = mock(DelegateExecution.class);
        pool = mock(Pool.class);

        when(pool.getExpectedSize()).thenReturn(EXPECTED_SIZE);
        when(execution.getProcessBusinessKey()).thenReturn(BUSINESS_KEY);

        collector = new ProcessVariablesCollector();
        collector.install(execution);
    }

    @Test
    public void testGenerateMachineIdsProducesExpectedOutput() throws Exception {
        GenerateListOfMachineIds generateListOfMachineIds = new GenerateListOfMachineIds();
        List<String> machineIds = generateListOfMachineIds.generateIdsFromBusinessKey(BUSINESS_KEY, EXPECTED_SIZE);

        assertThat(machineIds.size()).isEqualTo(EXPECTED_SIZE);
        final String firstId = Iterables.getFirst(machineIds, null);
        assertThat(firstId).startsWith("host-").contains(BUSINESS_KEY).endsWith("001");
        System.out.println(firstId);
        final String lastId = Iterables.getLast(machineIds);
        assertThat(lastId).startsWith("host-").contains(BUSINESS_KEY).endsWith(Integer.toString(EXPECTED_SIZE));
        System.out.println(lastId);
    }

    @Test
    public void testGENERATED_MACHINES_AND_GATEWAY_ProcessVariablesAreInitialized() throws Exception {
        GenerateListOfMachineIds generateListOfMachineIds = new GenerateListOfMachineIds();

        generateListOfMachineIds.execute(null, pool, execution);
        final List<String> machines = (List<String>) collector.getVariable(ProcessVariables.GENERATED_MACHINE_IDS);
        final String firstId = Iterables.getFirst(machines, null);
        final String gateway = (String) collector.getVariable(CoreProcessVariables.GATEWAY);

        assertThat(machines.size()).isEqualTo(EXPECTED_SIZE);
        assertThat(firstId).startsWith("host-").contains(BUSINESS_KEY).endsWith("001");
        assertThat(Iterables.getLast(machines)).startsWith("host-").contains(BUSINESS_KEY)
            .endsWith(Integer.toString(EXPECTED_SIZE));

        assertThat(gateway).isEqualTo(firstId);
    }
}

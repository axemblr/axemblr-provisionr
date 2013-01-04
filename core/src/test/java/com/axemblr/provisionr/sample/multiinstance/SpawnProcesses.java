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

package com.axemblr.provisionr.sample.multiinstance;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;

public class SpawnProcesses implements JavaDelegate {

    public static AtomicReference<RuntimeService> runtimeService = new AtomicReference<RuntimeService>();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        @SuppressWarnings("unchecked")
        List<String> people = (List<String>) execution.getVariable("people");

        for (final String person : people) {
            ProcessInstance instance = runtimeService.get().startProcessInstanceByKey("helloDude",
                ImmutableMap.<String, Object>of("singlePerson", person));
            System.out.println("Started process with ID " + instance.getId() + " for person " + person);
        }
    }
}

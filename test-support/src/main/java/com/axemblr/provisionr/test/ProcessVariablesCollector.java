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

package com.axemblr.provisionr.test;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.mockito.Matchers;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessVariablesCollector implements Answer<Void> {

    public static final Logger LOG = LoggerFactory.getLogger(ProcessVariablesCollector.class);

    private Map<String, Object> variables = Maps.newConcurrentMap();

    /**
     * Install the collector on the setVariable class
     */
    public void install(DelegateExecution execution) {
        doAnswer(this).when(execution).setVariable(Matchers.<String>any(), any());
    }

    @Override
    public Void answer(InvocationOnMock invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        LOG.info("Got method call {} with arguments {}",
            invocation.getMethod().getName(), Arrays.toString(arguments));

        variables.put((String) arguments[0], arguments[1]);
        return null;
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public String toString() {
        return "ProcessVariablesCollector{" +
            "variables=" + variables +
            '}';
    }
}

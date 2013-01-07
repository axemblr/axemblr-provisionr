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

import java.util.concurrent.TimeUnit;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class MultiInstanceIdempotentTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String aDude = String.class.cast(execution.getVariable("singlePerson"));

        if (aDude.equalsIgnoreCase("andrei")) {
            System.err.println("Long wait start: " + aDude);
            TimeUnit.SECONDS.sleep(5);
            System.err.println("Long wait done: " + aDude);

        } else {
            System.err.println("Short wait start: " + aDude);
            TimeUnit.SECONDS.sleep(3);
            System.err.println("Short wait done: " + aDude);
        }
    }
}

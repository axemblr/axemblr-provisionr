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

package com.axemblr.provisionr.core.activiti;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlwaysFailTask implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(AlwaysFailTask.class);

    public static final AtomicInteger COUNTER = new AtomicInteger(0);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        COUNTER.incrementAndGet();
        throw new RuntimeException("Failing. Time is " + new Date());
    }
}

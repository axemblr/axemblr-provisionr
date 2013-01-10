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

package com.axemblr.provisionr.sample.suspend;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailingTask implements JavaDelegate, Serializable {

    public static final AtomicInteger failCount = new AtomicInteger(0);

    private static final Logger LOG = LoggerFactory.getLogger(FailingTask.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        if (failCount.get() > 3) {
            LOG.info("Task failed {} times, not failing again", failCount.get());
        } else {
            LOG.info("Failing the task one more time for a total of " + failCount.get());
            throw new RuntimeException("FailingTask failed " + failCount.incrementAndGet() + " times");
        }
    }
}

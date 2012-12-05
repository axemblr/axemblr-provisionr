/*
 * Copyright 2012 Cisco Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.karaf.commands;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.After;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Srinivasan Chikkala
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti.cfg.xml")
public abstract class ActivitiTestCase {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    @Rule
    public ActivitiRule activitiSpringRule;

    @After
    public void closeProcessEngine() {
        // Required, since all the other tests seem to do a specific drop on the
        // end
        processEngine.close();
    }

    protected ProcessEngine getProcessEngine() {
        return this.processEngine;
    }

    protected ProcessInstance startProcess(String processKey) throws Exception {
        return startProcess(processKey, new HashMap<String, Object>());
    }

    protected ProcessInstance startProcess(String processKey, Map<String, Object> variables) throws Exception {

        ProcessInstance processInstance = runtimeService
            .startProcessInstanceByKey(processKey, variables);
        String id = processInstance.getId();
        System.out.println("Started process instance id " + id);
        long count = runtimeService.createProcessInstanceQuery().count();

        // Assert.assertEquals(0, count);

        HistoricProcessInstance historicProcessInstance = processEngine
            .getHistoryService().createHistoricProcessInstanceQuery()
            .processInstanceId(id).singleResult();

        // Assert.assertNotNull(historicProcessInstance);
        if (historicProcessInstance != null) {
            System.out.println("Finished " + processKey + "Instance. took "
                + historicProcessInstance.getDurationInMillis() + " millis");
        }
        return processInstance;
    }


}

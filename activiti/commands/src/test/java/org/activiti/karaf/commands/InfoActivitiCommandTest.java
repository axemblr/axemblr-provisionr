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

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Srinivasan Chikkala
 */
public class InfoActivitiCommandTest extends ActivitiTestCase {

    @Test
    @Deployment(resources = {"diagrams/test-bpm-1.bpmn20.xml", "diagrams/test-bpm-2.bpmn20.xml",
        "diagrams/test-bpm-3.bpmn20.xml"})
    public void testListBPMCommand1() throws Exception {
        // fail("The test case is a prototype.");
        String processKey = "Test-bpm-1";
        Map<String, Object> vars = new TreeMap<String, Object>();
        vars.put("myfoo", new Foo());
        vars.put("mybar", new Bar());
        vars.put("my.array", new Object[]{new Foo(), new Bar()});
        ProcessInstance pi = this.startProcess(processKey, vars);
        InfoActivitiCommand infoCmd = new InfoActivitiCommand();
        infoCmd.setProcessEngine(this.getProcessEngine());
        infoCmd.setInstanceID(pi.getId());
        infoCmd.doExecute();

        // DefaultActivitiPrintHandler printHandler = (DefaultActivitiPrintHandler)infoCmd.findBPMPrintHandler();
        // System.out.println("##### BPM PROCESS VARS JSON ####");
        // printHandler.printVariables(new PrintWriter(System.out, true), vars);

    }

    public static class Foo implements Serializable {

        private static final long serialVersionUID = 1L;
        private String myfoo = "foo2";
        private boolean yes;
        private String[] users = {"foo1", "foo2", "foo3"};
        private Bar fooBar = new Bar();

        public Bar getFooBar() {
            return fooBar;
        }

        public void setFooBar(Bar fooBar) {
            this.fooBar = fooBar;
        }

        public String getMyfoo() {
            return myfoo;
        }

        public void setMyfoo(String myfoo) {
            this.myfoo = myfoo;
        }

        public boolean isYes() {
            return yes;
        }

        public void setYes(boolean yes) {
            this.yes = yes;
        }

        public String[] getUsers() {
            return users;
        }

        public void setUsers(String[] users) {
            this.users = users;
        }

        public static Foo getFoo() {
            return new Foo();
        }
    }

    public static class Bar implements Serializable {

        private static final long serialVersionUID = 1L;
        private String mybar = "bar3";
        private boolean yes;
        private String[] users = {"bar1", "bar2", "bar3"};

        public String getMybar() {
            return mybar;
        }

        public void setMybar(String mybar) {
            this.mybar = mybar;
        }

        public boolean isYes() {
            return yes;
        }

        public void setYes(boolean yes) {
            this.yes = yes;
        }

        public String[] getUsers() {
            return users;
        }

        public void setUsers(String[] users) {
            this.users = users;
        }

    }

}

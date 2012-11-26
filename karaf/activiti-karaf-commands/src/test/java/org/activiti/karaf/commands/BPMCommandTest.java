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

import java.io.PrintWriter;

import org.activiti.karaf.commands.TextTable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Srinivasan Chikkala
 */
public class BPMCommandTest {
    
    public BPMCommandTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testTextTable1() {
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
        String[] headers = {"col1", "column2", "c3"};
        String[] row1 = {"myvalue111111111111111x", "myvalue2", "myvalue3"};
        String[] row2 = {"myvalue1", "myvalue2", "myvalue3"};
        String[] row3 = {"myvalue1", "myvalue2", "myvalue3"};
        TextTable tbl = new TextTable(3);
        tbl.addHeaders(headers);
        tbl.addRow(row1);
        tbl.addRow(row2);
        tbl.addRow(row3);
        PrintWriter out = new PrintWriter(System.out, true);
        tbl.print(out);
    }
    
    @Test
    public void testTextTable2() {
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
        String[] headers = {"col1", "column2", "c3"};
        String[] row1 = {"myvalue1", "myvalue2", "myvalue3"};
        String[] row2 = {"myvalue111111111111111111111111111111x", "myvalue2", "myvalue3"};
        String[] row3 = {"myvalue1", "myvalue2", "myvalue33333333333333333333x"};
        TextTable tbl = new TextTable(3);
        tbl.addHeaders(headers);
        tbl.addRow(row1);
        tbl.addRow(row2);
        tbl.addRow(row3);
        PrintWriter out = new PrintWriter(System.out, true);
        tbl.print(out);
    }

    @Test
    public void testTextTable3() {
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
        String[] headers = {"col1", "column2", "c3"};
        String[] row1 = {"myvalue1", "myvalue2", "myvalue3ddddddddddddddddddx"};
        String[] row2 = {"myvalue1", "myvalue2", "myvalue3"};
        String[] row3 = {"myvalue11111111111111111111111x", "myvalue2", "myvalue3"};
        TextTable tbl = new TextTable(3);
        tbl.addHeaders(headers);
        tbl.addRow(row1);
        tbl.addRow(row2);
        tbl.addRow(row3);
        PrintWriter out = new PrintWriter(System.out, true);
        tbl.print(out);
    }
    
}

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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import org.activiti.karaf.commands.util.TextTable;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Srinivasan Chikkala
 */
public class ActivitiCommandTest {

    private ByteArrayOutputStream backingStream;
    private PrintWriter out;

    @Before
    public void setUp() {
        backingStream = new ByteArrayOutputStream();
        out = new PrintWriter(backingStream);
    }

    @After
    public void tearDown() {
        out.close();
        backingStream.reset();
    }

    public String collectOutput() {
        out.flush();
        return backingStream.toString();
    }

    @Test
    public void testTextTable1() {
        String[] headers = {"col1", "column2", "c3"};
        String[] row1 = {"myvalue111111111111111x", "myvalue2", "myvalue3"};
        String[] row2 = {"myvalue1", "myvalue2", "myvalue3"};
        String[] row3 = {"myvalue1", "myvalue2", "myvalue3"};

        final String expectedOutput = " col1                     column2   c3       \n" +
            "[myvalue111111111111111x][myvalue2][myvalue3]\n" +
            "[myvalue1               ][myvalue2][myvalue3]\n" +
            "[myvalue1               ][myvalue2][myvalue3]\n";

        TextTable table = new TextTable(3);
        table.addHeaders(headers);
        table.addRow(row1);
        table.addRow(row2);
        table.addRow(row3);

        table.print(out);
        assertEquals(collectOutput(), expectedOutput);
    }

    @Test
    public void testTextTable2() {
        String[] headers = {"col1", "column2", "c3"};
        String[] row1 = {"myvalue1", "myvalue2", "myvalue3"};
        String[] row2 = {"myvalue111111111111111111111111111111x", "myvalue2", "myvalue3"};
        String[] row3 = {"myvalue1", "myvalue2", "myvalue33333333333333333333x"};

        final String expectedOutput = " col1                                    column2   c3                           \n" +
            "[myvalue1                              ][myvalue2][myvalue3                    ]\n" +
            "[myvalue111111111111111111111111111111x][myvalue2][myvalue3                    ]\n" +
            "[myvalue1                              ][myvalue2][myvalue33333333333333333333x]\n";

        TextTable table = new TextTable(3);
        table.addHeaders(headers);
        table.addRow(row1);
        table.addRow(row2);
        table.addRow(row3);

        table.print(out);
        assertEquals(collectOutput(), expectedOutput);
    }

    @Test
    public void testTextTable3() {
        String[] headers = {"col1", "column2", "c3"};
        String[] row1 = {"myvalue1", "myvalue2", "myvalue3ddddddddddddddddddx"};
        String[] row2 = {"myvalue1", "myvalue2", "myvalue3"};
        String[] row3 = {"myvalue11111111111111111111111x", "myvalue2", "myvalue3"};

        final String expectedOutput = " col1                             column2   c3                          \n" +
            "[myvalue1                       ][myvalue2][myvalue3ddddddddddddddddddx]\n" +
            "[myvalue1                       ][myvalue2][myvalue3                   ]\n" +
            "[myvalue11111111111111111111111x][myvalue2][myvalue3                   ]\n";

        TextTable table = new TextTable(3);
        table.addHeaders(headers);
        table.addRow(row1);
        table.addRow(row2);
        table.addRow(row3);

        table.print(out);
        assertEquals(collectOutput(), expectedOutput);
    }
}

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
package org.activiti.karaf.commands.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Srinivasan Chikkala
 */
public class TextTable {

    private static final int DEFAULT_WIDTH = 3;
    private int col;

    private List<String> columnHeaders;
    private List<Integer> columnWidths;
    private List<List<String>> rows;

    public TextTable(int col) {
        this.col = col;
        this.columnHeaders = new ArrayList<String>();
        this.columnWidths = new ArrayList<Integer>();
        this.rows = new ArrayList<List<String>>();
    }

    public void addHeaders(String... headers) {
        for (String header : headers) {
            this.columnHeaders.add(header);
            this.columnWidths.add(DEFAULT_WIDTH);
        }
    }

    public void addRow(String... colValues) {
        List<String> row = new ArrayList<String>();
        rows.add(row);
        if (colValues != null && colValues.length > this.col) {
            throw new IllegalArgumentException("Number of Column values passed are more than the tables " +
                "column cound " + this.col);
        }
        for (int i = 0; i < this.col; ++i) {
            String colValue = "";
            if ((i < colValues.length) && (colValues[i] != null)) {
                colValue = colValues[i];
                Integer colWidth = this.columnWidths.get(i);
                if (colWidth < colValue.length()) {
                    this.columnWidths.set(i, colValue.length());
                }
            }
            row.add(colValue);
        }
    }

    public void print(PrintStream stremaOut) {
        PrintWriter out = new PrintWriter(stremaOut, true);
        print(out);
    }

    public void print(PrintWriter out) {
        StringBuilder hdrFmtBuff = new StringBuilder();
        StringBuilder rowFmtBuff = new StringBuilder();
        // " %-20.20s   %-20.20s [%-20.20s]\n";
        // "[%-20.20s] [%-20.20s] [%-20.20s]\n";
        for (Integer width : this.columnWidths) {
            hdrFmtBuff.append(" %-").append(width).append(".").append(width).append("s ");
            rowFmtBuff.append("[%-").append(width).append(".").append(width).append("s]");
        }
        hdrFmtBuff.append("\n");
        rowFmtBuff.append("\n");
        String hdrFmt = hdrFmtBuff.toString();
        String rowFmt = rowFmtBuff.toString();

        out.printf(hdrFmt, this.columnHeaders.toArray());
        for (List<String> row : this.rows) {
            out.printf(rowFmt, row.toArray());
        }
    }

}

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * This class provides common utils for commands processing
 *
 * @author Srinivasan Chikkala
 */
public class Commands {

    public static final Commands UTIL = new Commands();

    public void printNameValues(PrintWriter out, Map<String, String> nvMap) {
        String fmt = "  %-16.16s %-20.60s\n";
        for (String key : nvMap.keySet()) {
            String value = nvMap.get(key);
            out.printf(fmt, key + ":", value);
        }
    }

    public String formatDate(Date date) {
        String dateTxt = "--";
        if (date != null) {
            dateTxt = DateFormat.getDateTimeInstance().format(date);
        }
        return dateTxt;
    }

    public String formatDuration(Long duration) {
        String dTxt = "--";
        if (duration != null) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            dTxt = format.format(new Date(duration));
        }
        return dTxt;
    }

    public void printText(PrintWriter out, Reader txtReader, String tabSpace) {
        BufferedReader in = new BufferedReader(txtReader);
        String line = null;
        try {
            while ((line = in.readLine()) != null) {
                out.printf("  %-16.16s %s\n", tabSpace, line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert a simple object to string.
     *
     * @param obj object .
     * @return string if the object is simple. else return null.
     */
    public String valueOf(Object obj) {
        String value = null;
        if (obj == null) {
            value = "NULL";
        } else if (obj.getClass().isPrimitive()
            || obj.getClass().isEnum()
            || obj instanceof java.lang.String) {
            value = obj.toString();
        } else if (obj instanceof Date) {
            value = formatDate((Date) obj);
        } else {
            value = null;
        }
        return value;
    }
}

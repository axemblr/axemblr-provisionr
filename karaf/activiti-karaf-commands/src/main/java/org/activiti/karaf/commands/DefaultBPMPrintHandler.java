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
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Default implementation of the process variable print handler.
 * 
 * @author Srinivasan Chikkala
 * 
 */
public class DefaultBPMPrintHandler extends AbstractBPMPrintHandler {
    
    private static final Logger LOG = Logger.getLogger(DefaultBPMPrintHandler.class.getName());

    protected void printVariable(PrintWriter out,String varName, Object varValue) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
                .create();
        
        LOG.info("Printing var " + varName);
        String jsonText = null;
        try {
            jsonText = gson.toJson(varValue);
        } catch (Exception ex) {
            jsonText = "{\n  " + varValue + "\n}"; // use default toString object            
            LOG.log(Level.INFO, "ERROR Serializing BPM Variable. " + ex.getMessage(), ex);
        }
        // out.printf("  %s\n", jsonText);
        CmdUtil.UTIL.printText(out, new StringReader(jsonText), " ");
    }

}

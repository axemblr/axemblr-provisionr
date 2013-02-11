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

package com.axemblr.provisionr.rundeck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.bind.JAXBException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class RundeckServletTest {

    private ByteArrayOutputStream output;
    private PrintWriter writer;

    @Before
    public void setUp() {
        output = new ByteArrayOutputStream();
        writer = new PrintWriter(output);
    }

    @After
    public void tearDown() throws IOException {
        writer.close();
        output.close();
    }

    @Test
    public void testEmptyListOfMachines() throws JAXBException, IOException, SAXException {
        ProcessEngine processEngine = new StandaloneInMemProcessEngineConfiguration().buildProcessEngine();
        try {
            RundeckServlet servlet = new RundeckServlet(processEngine);
            servlet.writeRundeckResourceModelXmlTo(writer);

            writer.flush();
            assertXMLEqual(output.toString(), "<project />");

        } finally {
            processEngine.close();
        }
    }
}

package com.axemblr.provisionr.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;

public abstract class BaseJaxbTest {

    protected JAXBContext jaxb;

    static {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);
    }

    @Before
    public void setUp() throws JAXBException {
        jaxb = JAXBContext.newInstance(getContextClasses());
    }

    public abstract Class[] getContextClasses();

    /**
     * Marshal an object as XML using a standard JAXB Context
     */
    protected String asXml(Object obj) throws Exception {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);

        Marshaller marshaller = jaxb.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        marshaller.marshal(obj, out);
        return outBytes.toString();
    }
}

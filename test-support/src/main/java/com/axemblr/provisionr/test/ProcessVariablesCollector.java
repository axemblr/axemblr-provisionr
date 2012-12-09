package com.axemblr.provisionr.test;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessVariablesCollector implements Answer<Void> {

    public static final Logger LOG = LoggerFactory.getLogger(ProcessVariablesCollector.class);

    private Map<String, Object> variables = Maps.newConcurrentMap();

    @Override
    public Void answer(InvocationOnMock invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        LOG.info("Got method call {} with arguments {}",
            invocation.getMethod().getName(), Arrays.toString(arguments));

        variables.put((String) arguments[0], arguments[1]);
        return null;
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }
}

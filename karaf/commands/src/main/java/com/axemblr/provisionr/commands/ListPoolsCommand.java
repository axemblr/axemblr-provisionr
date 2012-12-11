package com.axemblr.provisionr.commands;

import com.axemblr.provisionr.api.pool.Pool;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.PrintStream;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

@Command(scope = "provisionr", name = "list", description = "List active pools")
public class ListPoolsCommand extends OsgiCommandSupport {

    private static final PrintStream out = System.out;

    private final ProcessEngine processEngine;

    public ListPoolsCommand(ProcessEngine processEngine) {
        this.processEngine = checkNotNull(processEngine, "processEngine");
    }

    @Override
    protected Object doExecute() throws Exception {
        List<ProcessInstance> processes = processEngine.getRuntimeService()
            .createProcessInstanceQuery().list();

        if (processes.isEmpty()) {
            out.println("No active pools found. You can create one using provisionr:create");
        }
        for (ProcessInstance instance : processes) {
            Pool pool = (Pool) processEngine.getRuntimeService()
                .getVariable(instance.getId(), "pool");

            // TODO: try to retrieve the actual list of machines for this pool

            out.println(pool.toString());
            out.println("Business Key: " + instance.getBusinessKey());
            out.println();
        }

        return null;
    }
}

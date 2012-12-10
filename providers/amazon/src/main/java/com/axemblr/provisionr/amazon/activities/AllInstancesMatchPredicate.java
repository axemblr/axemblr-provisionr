package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.api.pool.Pool;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;

public abstract class AllInstancesMatchPredicate extends AmazonActivity {

    private final String resultVariable;
    private final Predicate<Instance> predicate;

    protected AllInstancesMatchPredicate(String resultVariable, Predicate<Instance> predicate) {
        this.resultVariable = checkNotNull(resultVariable, "resultVariable is null");
        this.predicate = checkNotNull(predicate, "predicate is null");
    }

    @Override
    public void execute(AmazonEC2 client, Pool pool, DelegateExecution execution) throws Exception {
        String[] instanceIds = (String[]) execution.getVariable(ProcessVariables.INSTANCES);
        checkNotNull(instanceIds, "process variable '{}' not found", ProcessVariables.INSTANCES);

        DescribeInstancesResult result = client.describeInstances(new DescribeInstancesRequest()
            .withInstanceIds(instanceIds));
        checkState(result.getReservations().size() == 1, "the instance ids are part of multiple reservations");

        List<Instance> instances = result.getReservations().get(0).getInstances();
        boolean allInstancesMatch = Iterables.all(instances, predicate);
        LOG.info(">> Checking {} instances with predicate {}. Result: {}", 
        		new Object[]{Arrays.toString(instanceIds), predicate, allInstancesMatch});
        execution.setVariable(resultVariable, allInstancesMatch);
    }
}

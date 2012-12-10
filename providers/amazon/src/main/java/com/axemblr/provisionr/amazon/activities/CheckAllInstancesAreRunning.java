package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.model.Instance;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.google.common.base.Predicate;

public class CheckAllInstancesAreRunning extends AllInstancesMatchPredicate {

    public static class InstanceIsRunning implements Predicate<Instance> {
        @Override
        public boolean apply(Instance instance) {
            return instance.getState().getName().equalsIgnoreCase("running");
        }
        
        @Override
        public String toString() {
        	return "InstanceIsRunning{}";
        }
    }

    public CheckAllInstancesAreRunning() {
        super(ProcessVariables.ALL_INSTANCES_RUNNING, new InstanceIsRunning());
    }
}

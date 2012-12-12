package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.model.Instance;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.google.common.base.Predicate;

public class CheckAllInstancesAreTerminated extends AllInstancesMatchPredicate {

    public static class InstanceIsTerminated implements Predicate<Instance> {
        @Override
        public boolean apply(Instance instance) {
            return instance.getState().getName().equalsIgnoreCase("terminated");
        }

        @Override
        public String toString() {
            return "InstanceIsTerminated{}";
        }
    }

    public CheckAllInstancesAreTerminated(ProviderClientCache cache) {
        super(cache, ProcessVariables.ALL_INSTANCES_TERMINATED, new InstanceIsTerminated());
    }
}

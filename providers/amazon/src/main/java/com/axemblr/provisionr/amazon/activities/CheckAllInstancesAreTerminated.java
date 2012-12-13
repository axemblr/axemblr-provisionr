/*
 * Copyright (c) 2012 S.C. Axemblr Software Solutions S.R.L
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

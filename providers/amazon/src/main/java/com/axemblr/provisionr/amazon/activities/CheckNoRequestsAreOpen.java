package com.axemblr.provisionr.amazon.activities;

import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.axemblr.provisionr.amazon.ProcessVariables;
import com.axemblr.provisionr.amazon.core.ProviderClientCache;
import com.google.common.base.Predicate;

public class CheckNoRequestsAreOpen extends AllSpotRequestsMatchPredicate {

    public static class RequestIsNotOpen implements Predicate<SpotInstanceRequest> {

        @Override
        public boolean apply(SpotInstanceRequest request) {
            return !"open".equalsIgnoreCase(request.getState());
        }

        @Override
        public String toString() {
            return "RequestIsNotOpen{}";
        }
    }

    public CheckNoRequestsAreOpen(ProviderClientCache cache) {
        super(cache, ProcessVariables.NO_SPOT_INSTANCE_REQUESTS_OPEN, new RequestIsNotOpen());
    }
}

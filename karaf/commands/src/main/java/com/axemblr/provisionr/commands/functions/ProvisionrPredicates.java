package com.axemblr.provisionr.commands.functions;

import com.axemblr.provisionr.api.Provisionr;
import com.google.common.base.Predicate;

public class ProvisionrPredicates {

    private ProvisionrPredicates() {
    }

    public static Predicate<Provisionr> withId(final String id) {
        return new Predicate<Provisionr>() {

            @Override
            public boolean apply(Provisionr candidate) {
                return candidate.getId().equals(id);
            }

            @Override
            public String toString() {
                return "WithId{id=" + id + "}";
            }
        };
    }
}

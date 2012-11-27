package com.axemblr.provisionr.amazon.activities;

import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.base.Function;
import java.io.Serializable;

public class CheckCredentials implements Function<Provider, Boolean>, Serializable {

    @Override
    public Boolean apply(Provider provider) {
        System.err.println("Validating provider: " + provider);
        return true;
    }
}

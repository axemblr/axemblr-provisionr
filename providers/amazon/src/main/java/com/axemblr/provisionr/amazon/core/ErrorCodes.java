package com.axemblr.provisionr.amazon.core;

public class ErrorCodes {

    private ErrorCodes() {
        /* singleton */
    }

    public static final String SECURITY_GROUP_NOT_FOUND = "InvalidGroup.NotFound";

    public static final String DUPLICATE_SECURITY_GROUP = "InvalidGroup.Duplicate";

    public static final String DUPLICATE_KEYPAIR = "InvalidKeyPair.Duplicate";

    public static final String KEYPAIR_NOT_FOUND = "InvalidKeyPair.NotFound";

}                         

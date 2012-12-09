package com.axemblr.provisionr.amazon.core;

public class SecurityGroups {

    private SecurityGroups() {
        /* singleton */
    }

    public static String formatNameFromBusinessKey(String businessKey) {
        return String.format("network-%s", businessKey);
    }

}

package com.axemblr.provisionr.amazon;

public class SecurityGroups {

    private SecurityGroups() {
        /* singleton */
    }

    public static String formatNameFromBusinessKey(String businessKey) {
        return String.format("network-%s", businessKey);
    }

}

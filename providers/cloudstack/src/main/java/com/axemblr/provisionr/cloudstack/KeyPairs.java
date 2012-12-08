package com.axemblr.provisionr.cloudstack;

public class KeyPairs {

    private KeyPairs() {
        // singleton
    }

    public static String formatNameFromBusinessKey(String businessKey) {
        return String.format("key-%s", businessKey);
    }
}

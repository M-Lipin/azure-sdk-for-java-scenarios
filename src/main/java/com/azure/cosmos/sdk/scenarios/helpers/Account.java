package com.azure.cosmos.sdk.scenarios.helpers;

public class Account {
    private  String host;
    private  String key;

    public String getHost() {
        return host;
    }

    public String getKey() {
        return key;
    }

    public Account(String host, String key) {
        this.host = host;
        this.key = key;
    }
}

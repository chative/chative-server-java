package org.whispersystems.textsecuregcm.configuration;

import java.net.URI;

public class RedisAddressConf {
    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public int getDatabase() {
        return database;
    }

    private String address;
    private String password;
    private int database;

    public RedisAddressConf(String url) {
        address = url;

        URI uri = URI.create(url);
        String auth = uri.getUserInfo();
        if (null != auth && auth.contains(":")) {
            password = auth.split(":")[1];
        }

        String replicaPath = uri.getPath();
        if (null != replicaPath && replicaPath.length() > 1) {
            replicaPath = replicaPath.substring(1);
            database = Integer.parseInt(replicaPath);
        }
    }
}

package org.whispersystems.textsecuregcm.storage;

public class ClientInfoRow {
    private String dftVersion;
    private String os;
    private String ua;

    public String getDftVersion() {
        return dftVersion;
    }

    public String getOs() {
        return os;
    }
    public String getUa() {
        return ua;
    }

    public ClientInfoRow(String dftVersion, String os, String ua) {
        this.dftVersion = dftVersion;
        this.os = os;
        this.ua = ua;
    }
}

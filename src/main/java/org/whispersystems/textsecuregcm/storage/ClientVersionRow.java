package org.whispersystems.textsecuregcm.storage;

public class ClientVersionRow {
    private String dft_version;

    private int count;

    public String getDft_version() {
        return dft_version;
    }

    public int getCount() {
        return count;
    }


    public ClientVersionRow(String dft_version, int count) {
        this.dft_version = dft_version;
        this.count = count;
    }
}

package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountDemoRow {

    @JsonProperty
    private int count;


    public CountDemoRow() {
    }

    public CountDemoRow(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}

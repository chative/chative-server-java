package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;


public class DistributedLockConfig {
    public String getUrl() {
        return url;
    }
    public List<String> getReplicaUrls() {
        return replicaUrls;
    }

    @JsonProperty
    @NotEmpty
    private String url;


    @JsonProperty
    private List<String> replicaUrls;


    @JsonProperty
    private int leaseTime;



    public long getLeaseTime() {
        if (leaseTime <= 0 ) {
            return 10;
        }
        return leaseTime;
    }
}



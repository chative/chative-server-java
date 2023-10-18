package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.whispersystems.textsecuregcm.util.ParameterValidator;

import java.util.List;

public class GetContactsRequest {
    @JsonProperty
    private List<String> uids;

    public List<String> getUids() {
        return uids;
    }

    public void validate(Logger logger) {
        if (null != uids) {
            for (String uid : uids) {
                if (!ParameterValidator.validateNumber(uid)) {
                    BaseResponse.err(BaseResponse.STATUS.INVALID_PARAMETER, "invalid uid: " + uid, logger);
                    break;
                }
            }
        }
    }
}

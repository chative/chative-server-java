package org.whispersystems.textsecuregcm.exceptions;

public class RegisterException extends RuntimeException {
    public RegisterException( String reason) {
        super();
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    private String reason;
}

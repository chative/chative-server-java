package org.whispersystems.textsecuregcm.entities;


public class InterrupterProcessException extends Exception {
    private int status;
    private String reason;

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }


    public InterrupterProcessException(String message, Throwable cause, int status, String reason) {
        super(message, cause);
        this.status = status;
        this.reason = reason;
    }

    public InterrupterProcessException(int status, String reason) {
        this.status = status;
        this.reason = reason;
    }
}

package org.whispersystems.textsecuregcm.controllers;

public class StaleRecipientException  {
    private final String destination;

    public String getIdentityKey() {
        return identityKey;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    private final String identityKey;
    private final int registrationId;
    
    public StaleRecipientException(String destination, String identityKey, int registrationId) {
        this.destination = destination;
        this.identityKey = identityKey;
        this.registrationId = registrationId;
    }
    
    public String getDestination() {
        return destination;
    }
}

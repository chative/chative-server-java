package org.whispersystems.textsecuregcm.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Optional;

public class RedPhoneConfiguration {

  @JsonProperty
  private String authKey;

  public Optional<byte[]> getAuthorizationKey() throws DecoderException {
    if (authKey == null || authKey.trim().length() == 0) {
      return Optional.empty();
    }

    return Optional.of(Hex.decodeHex(authKey.toCharArray()));
  }
}

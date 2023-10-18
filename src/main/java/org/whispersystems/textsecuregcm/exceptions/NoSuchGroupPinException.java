package org.whispersystems.textsecuregcm.exceptions;

public class NoSuchGroupPinException extends RuntimeException {
  static final long serialVersionUID = -3387516993124229948L;

  public NoSuchGroupPinException() {
      super();
  }

  public NoSuchGroupPinException(String explanation) {
      super(explanation);
  }
}

package org.whispersystems.textsecuregcm.exceptions;

public class InvalidParameterException extends RuntimeException {
  static final long serialVersionUID = -3387516993124229948L;

  public InvalidParameterException() {
      super();
  }

  public InvalidParameterException(String explanation) {
      super(explanation);
  }
}

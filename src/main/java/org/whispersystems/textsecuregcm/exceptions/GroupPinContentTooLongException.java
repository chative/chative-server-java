package org.whispersystems.textsecuregcm.exceptions;

public class GroupPinContentTooLongException extends RuntimeException {
  static final long serialVersionUID = -3387516993124229948L;

  public GroupPinContentTooLongException() {
      super();
  }

  public GroupPinContentTooLongException(String explanation) {
      super(explanation);
  }
}

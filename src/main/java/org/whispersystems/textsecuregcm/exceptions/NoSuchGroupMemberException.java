package org.whispersystems.textsecuregcm.exceptions;

public class NoSuchGroupMemberException extends RuntimeException {
  static final long serialVersionUID = -3387516993124229948L;

  public NoSuchGroupMemberException() {
      super();
  }

  public NoSuchGroupMemberException(String explanation) {
      super(explanation);
  }
}

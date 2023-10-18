package org.whispersystems.textsecuregcm.exceptions;

public class NoPermissionException extends RuntimeException {
  static final long serialVersionUID = -3387516993124229948L;

  public NoPermissionException() {
      super();
  }

  public NoPermissionException(String explanation) {
      super(explanation);
  }
}

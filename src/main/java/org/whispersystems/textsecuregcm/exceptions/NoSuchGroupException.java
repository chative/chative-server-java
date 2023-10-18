package org.whispersystems.textsecuregcm.exceptions;

public class NoSuchGroupException extends RuntimeException {
  static final long serialVersionUID = -3387516993124229948L;

  public NoSuchGroupException() {
      super();
  }

  public NoSuchGroupException(String explanation) {
      super(explanation);
  }
}

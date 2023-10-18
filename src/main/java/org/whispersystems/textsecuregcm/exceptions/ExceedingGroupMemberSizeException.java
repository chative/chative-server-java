package org.whispersystems.textsecuregcm.exceptions;

public class ExceedingGroupMemberSizeException extends RuntimeException {
  static final long serialVersionUID = -3387516993124229948L;

  public ExceedingGroupMemberSizeException() {
      super();
  }

  public ExceedingGroupMemberSizeException(String explanation) {
      super(explanation);
  }
}

package org.whispersystems.textsecuregcm.exceptions;

public class NoSuchGroupAnnouncementException extends RuntimeException {
  static final long serialVersionUID = -3387516993124229948L;

  public NoSuchGroupAnnouncementException() {
      super();
  }

  public NoSuchGroupAnnouncementException(String explanation) {
      super(explanation);
  }
}

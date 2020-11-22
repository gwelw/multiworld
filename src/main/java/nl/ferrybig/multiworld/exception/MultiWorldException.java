package nl.ferrybig.multiworld.exception;

public class MultiWorldException extends Exception {

  private static final long serialVersionUID = 4546553324656743L;

  public MultiWorldException() {
    super();
  }

  public MultiWorldException(Throwable cause) {
    super(cause);
  }

  public MultiWorldException(String message) {
    super(message);
  }

  public MultiWorldException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
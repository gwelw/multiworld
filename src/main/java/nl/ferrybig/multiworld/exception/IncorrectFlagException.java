package nl.ferrybig.multiworld.exception;

public class IncorrectFlagException extends RuntimeException {

  public IncorrectFlagException() {
  }

  public IncorrectFlagException(String message) {
    super(message);
  }

  public IncorrectFlagException(Throwable cause) {
    super(cause);
  }

  public IncorrectFlagException(String message, Throwable cause) {
    super(message, cause);
  }
}

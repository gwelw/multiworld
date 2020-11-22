package nl.ferrybig.multiworld.exception;

public class CommandException extends MultiWorldException {

  private static final long serialVersionUID = 1L;

  public CommandException() {
  }

  public CommandException(String msg) {
    super(msg);
  }

  public CommandException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public CommandException(Throwable cause) {
    super(cause);
  }
}

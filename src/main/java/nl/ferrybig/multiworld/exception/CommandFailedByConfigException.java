package nl.ferrybig.multiworld.exception;

public class CommandFailedByConfigException extends CommandException {

  private static final long serialVersionUID = 1L;

  public CommandFailedByConfigException() {
  }

  public CommandFailedByConfigException(String msg) {
    super(msg);
  }

  public CommandFailedByConfigException(Throwable ex) {
    super(ex);
  }

  public CommandFailedByConfigException(String msg, Throwable ex) {
    super(msg, ex);
  }
}

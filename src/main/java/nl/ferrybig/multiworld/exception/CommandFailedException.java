package nl.ferrybig.multiworld.exception;

public class CommandFailedException extends CommandException {

  private static final long serialVersionUID = 1L;

  public CommandFailedException() {
  }

  public CommandFailedException(String msg) {
    super(msg);
  }
}

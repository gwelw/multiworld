package nl.ferrybig.multiworld.exception;

public class InvalidFlagException extends CommandException {

  private static final long serialVersionUID = 224457743535694L;

  public InvalidFlagException() {
    super("Unknown flag specified");
  }
}
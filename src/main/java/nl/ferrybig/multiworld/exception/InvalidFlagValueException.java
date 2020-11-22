package nl.ferrybig.multiworld.exception;

public class InvalidFlagValueException extends CommandException {

  private static final long serialVersionUID = 984984267265589118L;

  public InvalidFlagValueException() {
    super("Unknown value specified");
  }
}
package nl.ferrybig.multiworld.exception;

public class NotEnabledException extends CommandException {

  private static final long serialVersionUID = 11212324335456L;

  public NotEnabledException() {
    super("The function you tried to use is not enabled");
  }
}
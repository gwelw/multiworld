package nl.ferrybig.multiworld.exception;

public class PermissionException extends CommandException {

  private static final long serialVersionUID = 498329828736746745L;

  public PermissionException() {
    super("You dont have permissions");
  }
}

package nl.ferrybig.multiworld.exception;

public class ArgumentException extends CommandException {

  private static final long serialVersionUID = 22355441L;

  private final String usage;

  public ArgumentException() {
    super("Illegal arguments specified");
    this.usage = null;
  }

  public ArgumentException(String correctUsage) {
    super("Illegal arguments specified, usage: " + correctUsage);
    this.usage = correctUsage;
  }

  public String correctUsage() {
    return this.usage;
  }
}

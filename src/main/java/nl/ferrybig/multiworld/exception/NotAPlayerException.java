package nl.ferrybig.multiworld.exception;

public class NotAPlayerException extends CommandException {

  public NotAPlayerException() {
  }

  public NotAPlayerException(String msg) {
    super(msg);
  }
}

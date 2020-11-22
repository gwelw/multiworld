package nl.ferrybig.multiworld.exception;

public class InvalidWorldGenOptionsException extends WorldGenException {

  private static final long serialVersionUID = 1120334705430567335L;

  public InvalidWorldGenOptionsException() {
    super("Unknown options specified to world gen");
  }

  public InvalidWorldGenOptionsException(String msg) {
    super(msg);
  }
}

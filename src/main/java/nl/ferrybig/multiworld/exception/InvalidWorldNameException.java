package nl.ferrybig.multiworld.exception;

public class InvalidWorldNameException extends UnknownWorldException {

  private static final long serialVersionUID = 1L;

  public InvalidWorldNameException(String world) {
    super(world + ", Invalid world syntacs");
  }
}

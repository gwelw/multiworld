package nl.ferrybig.multiworld.exception;

public class WorldGenException extends MultiWorldException {

  private static final long serialVersionUID = 90537768650427224L;

  public WorldGenException() {
  }

  public WorldGenException(String msg) {
    super(msg);
  }

  public WorldGenException(Throwable t) {
    super(t);
  }

  public WorldGenException(Throwable t, String msg) {
    super(msg, t);
  }
}

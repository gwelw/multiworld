package nl.ferrybig.multiworld.exception;

public class ConfigException extends MultiWorldException {

  private static final long serialVersionUID = 5397583331684367L;

  public ConfigException() {
  }

  public ConfigException(String msg) {
    super(msg);
  }

  public ConfigException(Throwable cause) {
    super(cause);
  }

  public ConfigException(String msg, Throwable cause) {
    super(msg, cause);
  }
}

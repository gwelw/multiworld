package nl.ferrybig.multiworld.exception;

public class ConfigurationSaveException extends MultiWorldException {

  public ConfigurationSaveException() {
  }

  public ConfigurationSaveException(String msg) {
    super(msg);
  }

  public ConfigurationSaveException(Throwable cause) {
    super(cause);
  }

  public ConfigurationSaveException(String msg, Throwable cause) {
    super(msg, cause);
  }
}

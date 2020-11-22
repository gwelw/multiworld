package nl.ferrybig.multiworld.api;

import nl.ferrybig.multiworld.exception.MultiWorldException;

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

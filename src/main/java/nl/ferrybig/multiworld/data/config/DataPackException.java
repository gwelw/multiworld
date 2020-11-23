package nl.ferrybig.multiworld.data.config;

import nl.ferrybig.multiworld.exception.MultiWorldException;

public class DataPackException extends MultiWorldException {

  private static final long serialVersionUID = 1L;

  public DataPackException(String msg) {
    super(msg);
  }

  public DataPackException(String msg, Throwable cause) {
    super(msg, cause);
  }
}

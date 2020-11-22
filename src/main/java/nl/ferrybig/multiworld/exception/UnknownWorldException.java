package nl.ferrybig.multiworld.exception;

import java.beans.ConstructorProperties;

public class UnknownWorldException extends CommandException {

  private static final long serialVersionUID = 2203948382489392L;

  private final String world;

  public UnknownWorldException() {
    super("Cannot find the world specified");
    this.world = "";
  }

  @ConstructorProperties("wrongWorld")
  public UnknownWorldException(String world) {
    super("cannot find world: " + world);
    this.world = world;
  }

  public String getWrongWorld() {
    return world;
  }
}
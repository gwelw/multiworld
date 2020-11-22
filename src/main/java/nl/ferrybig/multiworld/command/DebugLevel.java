package nl.ferrybig.multiworld.command;

public enum DebugLevel {
  VVVVVV(-1),
  VVVVV(0),
  VVVV(1),
  VVV(2),
  VV(3),
  V(4),
  NONE(Integer.MAX_VALUE);
  private final int level;

  DebugLevel(int level) {
    this.level = level;
  }

  public int getLevel() {
    return level;
  }
}

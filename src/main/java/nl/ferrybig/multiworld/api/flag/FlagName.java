package nl.ferrybig.multiworld.api.flag;

import java.util.Locale;
import nl.ferrybig.multiworld.exception.InvalidFlagException;

public enum FlagName {
  SPAWN_MONSTER("SpawnMonster", true),
  SPAWN_ANIMAL("SpawnAnimal", true),
  PVP("PvP", true),
  REMEMBER_SPAWN("RememberSpawn", true),
  CREATIVE_WORLD("CreativeWorld", false),
  SAVE_ON("SaveOn", true),
  RECEIVE_CHAT("RecieveChat", true),
  SEND_CHAT("SendChat", true);

  private final String userFriendlyName;
  private final boolean defaultState;

  FlagName(String name, boolean defaultState) {
    userFriendlyName = name;
    this.defaultState = defaultState;
  }

  public static FlagName getFlagFromString(String str) throws InvalidFlagException {
    try {
      return FlagName.valueOf(FlagName.class, str.toUpperCase(Locale.ENGLISH));
    } catch (IllegalArgumentException e) {
      throw (InvalidFlagException) new InvalidFlagException().initCause(e);
    }
  }

  public static String makeFlagList() {
    FlagName[] flags = FlagName.class.getEnumConstants();
    StringBuilder out = new StringBuilder().append("The flags: ");
    boolean first = true;
    for (FlagName flag : flags) {
      if (!first) {
        out.append(", ");
      }
      out.append(flag.toString());
      first = false;
    }
    return out.toString();
  }

  @Override
  public String toString() {
    return userFriendlyName;
  }

  public boolean getDefaultState() {
    return defaultState;
  }
}
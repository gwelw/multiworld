package nl.ferrybig.multiworld.command;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class ArgumentType {

  private static final WeakHashMap<String, WeakReference<ArgumentType>> map = new WeakHashMap<>();
  public static final ArgumentType FLAG_VALUE = valueOf("<Flag Value>");
  public static final ArgumentType NEW_WORLD_NAME = valueOf("<New World Name>");
  public static final ArgumentType TARGET_PLAYER = valueOf("<Target Player>");
  public static final ArgumentType TARGET_WORLD = valueOf("<Target World>");
  private final MessageProvider messages;

  private ArgumentType(final String message) {
    this.messages = () -> message;
  }

  public static ArgumentType valueOf(String name) {
    assert map
        != null; // If map is null, somebody have moved the map below the argument types definition
    WeakReference<ArgumentType> m = map.get(name);
    ArgumentType argumentType = null;
    if (m != null) {
      argumentType = m.get();
    }
    if (argumentType == null) {
      argumentType = new ArgumentType(name);
      m = new WeakReference<>(argumentType);
      map.put(name, m);
    }
    return argumentType;
  }

  public String getMessage() {
    return this.messages.getMessage();
  }

  private interface MessageProvider {
    String getMessage();
  }
}

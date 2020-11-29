package nl.ferrybig.multiworld.chat;

import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import java.util.Arrays;
import nl.ferrybig.multiworld.flags.FlagValue;
import nl.ferrybig.multiworld.generator.SpeedLevel;
import org.bukkit.ChatColor;

public class Formatter {

  private static final String BOOLEAN_TRUE = GREEN + "True";
  private static final String BOOLEAN_FALSE = RED + "False";
  private static final String UNKNOWN_FLAG = GOLD + "Unknown";

  private Formatter() {
  }

  public static String printBoolean(boolean b) {
    return (b ? BOOLEAN_TRUE : BOOLEAN_FALSE);
  }

  public static String printFlag(FlagValue flag) {
    if (flag == FlagValue.UNKNOWN) {
      return UNKNOWN_FLAG;
    }
    return printBoolean(flag.getAsBoolean());
  }

  public static String printObject(Object input) {
    if (input instanceof Boolean) {
      return printBoolean((Boolean) input);
    }
    if (input instanceof FlagValue) {
      return printFlag((FlagValue) input);
    }
    if (input instanceof String) {
      return (String) input;
    }
    return input.toString();
  }

  public static String createList(Object... args) {
    return createList(GOLD, args);
  }

  public static String createList(ChatColor color, Object... args) {
    ChatColor[] colors = new ChatColor[args.length];
    Arrays.fill(colors, color);
    return createList(colors, args);
  }

  public static String createList(ChatColor[] color, Object... args) {
    if (color.length != args.length) {
      throw new IllegalArgumentException(color.length + "!=" + args.length);
    }
    StringBuilder out = new StringBuilder();
    out.append(BLUE).append("[");
    for (int i = 0; i < args.length; i++) {
      out.append(color[i]).append(args[i]).append(BLUE).append(", ");
    }
    out.setLength(out.length() - 2);
    out.append(BLUE).append("]");
    return out.toString();
  }

  public static String printSpeed(SpeedLevel speedLevel) {
    return speedLevel.name();
  }
}

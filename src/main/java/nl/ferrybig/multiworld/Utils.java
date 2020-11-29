package nl.ferrybig.multiworld;

import java.util.ArrayList;
import java.util.Arrays;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.handler.DataHandler;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.UnknownWorldException;
import nl.ferrybig.multiworld.translation.Translation;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.util.ChatPaginator;

public final class Utils {

  public static final String COMMAND_STARTER = "command.";
  public static final String PERMISSION_STARTER = "multiworld.";

  private Utils() {
  }

  public static boolean canUseCommand(CommandStack sender, String command) {
    if (!hasPermission(sender, COMMAND_STARTER.concat(command))) {
      sender.sendMessage(MessageType.ERROR, Translation.LACKING_PERMISSIONS);
      return false;
    }
    return true;
  }

  public static boolean checkWorldName(String name) {
    if (name.isEmpty()) {
      return false;
    }
    if (Character.isLetterOrDigit(name.charAt(0)) && Character
        .isLetterOrDigit(name.charAt(name.length() - 1))) {
      for (char i : name.toCharArray()) {
        if (!Character.isLetterOrDigit(i) && Character.getType(i) != Character.SPACE_SEPARATOR
            && i != '_' && i != '-' && i != ',') {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }

  public static InternalWorld getWorld(String name, DataHandler handler, boolean mustBeLoaded)
      throws UnknownWorldException {
    Utils.checkWorldName(name);
    InternalWorld worldObj = handler.getWorldManager().getInternalWorld(name, mustBeLoaded);
    if (worldObj == null) {
      throw new UnknownWorldException(name);
    }
    return worldObj;
  }

  public static boolean hasPermission(CommandStack stack, String permission) {
    return stack.hasPermission(PERMISSION_STARTER + permission);
  }

  public static boolean hasPermission(CommandSender sender, String permission) {
    return sender.hasPermission(PERMISSION_STARTER + permission);
  }

  public static String[] parseArguments(String[] arguments) {
    int numberOfArguments = arguments.length;
    boolean hasFoundToken = false;
    ArrayList<String> argList = new ArrayList<>(numberOfArguments);
    StringBuilder tmp = null;
    int index = 0;
    for (String argument : arguments) {
      int quotes = Integer.numberOfTrailingZeros(argument.concat(" ").split("\"").length - 1);
      if (quotes == 0) {
        hasFoundToken = !hasFoundToken;
        if (hasFoundToken) {
          numberOfArguments--;
          tmp = new StringBuilder(argument.concat(" "));
        } else {
          argList.add(index++, tmp.toString().concat(argument)
              .replaceAll("\"\"", "\u0000")
              .replaceAll("\"", "")
              .replaceAll("\u0000", "\""));
          tmp = null;
        }
      } else if (hasFoundToken) {
        numberOfArguments--;
        tmp.append(argument).append(" ");
      } else {
        argList.add(index++, argument
            .replaceAll("\"\"", "\u0000")
            .replaceAll("\"", "")
            .replaceAll("\u0000", "\""));
      }
    }
    if (tmp != null) {
      argList.add(tmp.toString());
    }
    return argList.toArray(new String[argList.size()]);
  }

  public static void sendMessage(CommandSender s, String msg) {
    sendMessage(s, msg, 5);
  }

  public static void sendMessage(CommandSender s, String msg, int spaces) {
    char[] spaceChars = new char[spaces];
    Arrays.fill(spaceChars, ' ');
    String spaceString = new String(spaceChars);
    sendMessage(s, msg, spaceString, false);
  }

  public static void sendMessage(CommandSender s, String msg, String prefix,
      boolean addPrefixToFirstOutput) {
    if (msg.contains("\n")) {
      for (String str : msg.split("\n")) {
        sendMessage0(s, str, prefix, addPrefixToFirstOutput);
        // addded another method to make this call less expensive since .contains on a long string takes long
      }
      return;
    }
    sendMessage0(s, msg, prefix, addPrefixToFirstOutput);
  }

  private static void sendMessage0(CommandSender sender, String message, String prefix,
      boolean addPrefixToFirstOutput) {
    if (sender instanceof ConsoleCommandSender) {
      if (addPrefixToFirstOutput) {
        sender.sendMessage(prefix + message);
      } else {
        sender.sendMessage(message);
      }
      return;
    }

    final int prefixSubstract = countOccurrences(prefix, ChatColor.COLOR_CHAR) * 2;
    final int prefixLength = prefix.length() - prefixSubstract;
    final int maxLineLenght = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH;

    if ((message.length() + (addPrefixToFirstOutput ? prefixLength : 0)) > maxLineLenght) {
      int lastIndex = prefix.lastIndexOf(ChatColor.COLOR_CHAR);
      char color = lastIndex != -1 ? prefix.charAt(lastIndex + 1) : 'f';
      int charsLeft = 60;
      String[] parts = message.split(" ");
      StringBuilder stringBuilder = new StringBuilder(maxLineLenght);
      if (addPrefixToFirstOutput) {
        stringBuilder.append(prefix);
        charsLeft -= prefixLength;
      }
      for (String part : parts) {
        if (part.lastIndexOf(0x00A7) != -1) {
          assert part.lastIndexOf(0x00A7) + 1 < part.length();
          color = part.charAt(part.lastIndexOf(0x00A7) + 1);
        }
        if ((charsLeft - part.length()) < 1) {
          sender.sendMessage(stringBuilder.toString());
          charsLeft = maxLineLenght - prefixLength;
          stringBuilder.setLength(0);
          stringBuilder = new StringBuilder(maxLineLenght);
          stringBuilder.append(prefix);
          stringBuilder.append('\u00A7').append(color);
        }
        charsLeft -= part.length() + 1;
        charsLeft += countOccurrences(part, ChatColor.COLOR_CHAR) * 2;
        stringBuilder.append(part).append(" ");
      }
      if (stringBuilder.length() != 0) {
        sender.sendMessage(stringBuilder.toString());
      }
    } else {
      sender.sendMessage(addPrefixToFirstOutput ? prefix + message : message);
    }
  }

  public static int countOccurrences(String haystack, char needle) {
    int count = 0;
    char[] contents = haystack.toCharArray();
    for (char content : contents) {
      if (content == needle) {
        count++;
      }
    }
    return count;
  }
}

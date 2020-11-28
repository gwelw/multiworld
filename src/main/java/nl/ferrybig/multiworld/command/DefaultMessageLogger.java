package nl.ferrybig.multiworld.command;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.getLastColors;

import java.util.Arrays;
import java.util.Set;
import nl.ferrybig.multiworld.Utils;
import nl.ferrybig.multiworld.translation.message.PackedMessageData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permissible;

public class DefaultMessageLogger implements MessageLogger {

  public static final String DEFAULT_PREFIX =
      GOLD + "[" + GREEN + "MultiWorld" + GOLD + "] " + WHITE;

  private final String prefix;
  private final CommandSender receiver;
  private final String errorPrefix;
  private final String successPrefix;

  public DefaultMessageLogger(CommandSender receiver, String prefix) {
    this(receiver, prefix, RED.toString(), GREEN.toString());
  }

  public DefaultMessageLogger(CommandSender receiver, String prefix,
      String errorPrefix, String successPrefix) {
    this.receiver = receiver;
    this.prefix = prefix;
    this.errorPrefix = errorPrefix;
    this.successPrefix = successPrefix;
  }

  @Override
  public void sendMessage(MessageType type, String message) {
    StringBuilder builder = new StringBuilder();
    if (type != null) {
      if (type == MessageType.SUCCESS) {
        builder.append(successPrefix);
      } else if (type == MessageType.ERROR) {
        builder.append(errorPrefix);
      }
    }
    builder.append(message.replace(Command.RESET, getLastColors(prefix)));
    Utils.sendMessage(receiver, builder.toString(), prefix, true);
  }

  @Override
  public void sendMessage(MessageType type, PackedMessageData... message) {
    this.sendMessage(type, this.transformMessage(message));
  }

  @Override
  public void sendMessageBroadcast(MessageType type, String message) {
    sendMessageBroadcast(type, message, true);
  }

  public void sendMessageBroadcast(MessageType type, String message, boolean sendToConsole) {
    message = message.replace(Command.RESET, getLastColors(prefix));
    String result = prefix + receiver.getName() + ": " + message;
    if (receiver instanceof BlockCommandSender && ((BlockCommandSender) receiver).getBlock()
        .getWorld().getGameRuleValue("commandBlockOutput").equalsIgnoreCase("false")) {
      receiver.getServer().getConsoleSender().sendMessage(result);
      return;
    }
    Set<Permissible> users = receiver.getServer().getPluginManager()
        .getPermissionSubscriptions(Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
    String colored =
        prefix + GRAY + ITALIC + "[" + receiver.getName() + ": " + getLastColors(prefix) + message
            + GRAY + ITALIC + "]";
    if (!(receiver instanceof ConsoleCommandSender)) {
      receiver.sendMessage(prefix + message);
    }
    for (Permissible user : users) {
      if (user instanceof CommandSender) {
        CommandSender target = (CommandSender) user;
        if (target instanceof ConsoleCommandSender) {
          if (!sendToConsole && target == Bukkit.getConsoleSender()) {
            continue;

          }
          target.sendMessage(result);
        } else if (target != receiver) {
          target.sendMessage(colored);
        }
      }
    }
  }

  @Override
  public void sendMessageBroadcast(MessageType type, PackedMessageData... message) {
    this.sendMessageBroadcast(type, this.transformMessage(message),
        !Arrays.asList(message).contains(PackedMessageData.NO_CONSOLE_MESSAGE));
  }

  @Override
  public void sendMessageUsage(String commandLabel, ArgumentType... types) {
    StringBuilder build = new StringBuilder();
    build.append("Command Usage: /").append(commandLabel);
    for (ArgumentType type : types) {
      build.append(' ').append(type.getMessage());
    }
    this.sendMessage(MessageType.ERROR, build.toString());
  }

  public String transformMessage(PackedMessageData[] options) {
    String process = "";
    if (options.length != 0) {
      for (PackedMessageData option : options) {
        process = option.transformMessage(process);
      }
    }
    return process;
  }
}

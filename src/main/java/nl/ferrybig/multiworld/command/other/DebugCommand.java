package nl.ferrybig.multiworld.command.other;

import nl.ferrybig.multiworld.chat.Formatter;
import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.data.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class DebugCommand extends Command {

  private final VersionHandler versionHandler;

  public DebugCommand(VersionHandler versionHandler) {
    super("debug", "Prints outs some debug information");
    this.versionHandler = versionHandler;
  }

  @Override
  public void runCommand(CommandStack stack) {
    stack.sendMessage(MessageType.HIDDEN_SUCCESS, "Now printing debug information");
    stack.sendMessage(MessageType.HIDDEN_SUCCESS,
        "MultiWorld version: " + this.versionHandler.getVersion());
    stack.sendMessage(MessageType.HIDDEN_SUCCESS, "Bukkit version: " + Bukkit.getVersion());
    stack.sendMessage(MessageType.HIDDEN_SUCCESS, "");
    stack.sendMessage(MessageType.HIDDEN_SUCCESS, "--<[Modules]>--");
    stack.sendMessage(MessageType.HIDDEN_SUCCESS,
        Formatter.createList(ChatColor.WHITE, "State", "pluginName"));
    for (String plugin : versionHandler.getPlugins()) {
      stack.sendMessage(MessageType.HIDDEN_SUCCESS,
          Formatter.createList((versionHandler.isLoaded(plugin)
              ? (versionHandler.isEnabled(plugin) ? "Working" : "Loaded")
              : "Unloaded"), plugin));
    }
    stack.sendMessage(MessageType.HIDDEN_SUCCESS, "");
    stack.sendMessage(MessageType.HIDDEN_SUCCESS, "--<[CommandStacks]>--");
    CommandStack tmp = stack;
    do {
      StringBuilder sb = new StringBuilder();
      sb.append(tmp.getClass().getCanonicalName());
      sb.append("\n - /").append(tmp.getCommandLabel());
      sb.append(' ');
      String[] args = tmp.getArguments();
      if (args != null) {
        sb.append('[');
        for (String arg : args) {
          sb.append(arg).append(' ');
        }
        if (args.length != 0) {
          sb.setLength(sb.length() - 1);
        }
        sb.append(']');
      }
      stack.sendMessage(MessageType.HIDDEN_SUCCESS, sb.toString());
    }
    while ((tmp = tmp.getParent()) != null);
  }
}

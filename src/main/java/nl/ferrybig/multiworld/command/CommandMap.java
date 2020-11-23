package nl.ferrybig.multiworld.command;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import nl.ferrybig.multiworld.translation.Translation;
import org.bukkit.command.CommandSender;

public class CommandMap extends Command {

  private static final String[] nullString = new String[0];
  public final Map<String, Command> commands;
  public final Map<String, String> aliases;

  public CommandMap(String permissions, Map<String, Command> commands, Map<String, String> aliases) {
    super(permissions, "Base command");
    this.commands = Collections.unmodifiableMap(commands);
    this.aliases = aliases;
  }

  private void parseCommand(CommandStack sender, String name) {
    Command cmd = this.commands.get(name);
    if (cmd != null) {
      cmd.execute(sender);
    } else if (aliases != null) {
      String newName = aliases.get(name);
      if (newName != null) {
        this.parseCommand(sender, newName);
      } else {
        sender.sendMessage(MessageType.ERROR, Translation.COMMAND_NOT_FOUND);
      }
    } else {
      sender.sendMessage(MessageType.ERROR, Translation.COMMAND_NOT_FOUND);
    }
  }

  @Override
  public void runCommand(CommandStack s) {
    CommandStack newStack = s.newStack().popArguments(1).build();
    if (s.getArguments().length == 0) {
      this.parseCommand(newStack, "help");
    } else {
      this.parseCommand(newStack, s.getArguments()[0]);
    }
  }

  private String[] removeFirstFromArray(String[] input) {
    if (input.length < 2) {
      return nullString;
    }
    String[] output = new String[input.length - 1];
    System.arraycopy(input, 1, output, 0, output.length);
    return output;
  }

  public String[] getOptionsForUnfinishedCommands(CommandSender sender, String commandName,
      String[] split) {
    if (commandName.equalsIgnoreCase("nl/ferrybig/multiworld") || commandName
        .equalsIgnoreCase("mw")) {
      if (split.length == 0) {
        Set<String> commands = this.commands.keySet();
        return commands.toArray(new String[commands.size()]);
      } else if (split.length == 1) {
        Set<String> commands = new HashSet<>(this.commands.keySet());
        Set<String> found = new HashSet<>(commands.size());
        String lowerName = split[0].toLowerCase();
        for (String command : commands) {
          if (command.toLowerCase(Locale.ENGLISH).startsWith(lowerName)) {
            found.add(command);
          }
        }
        return found.toArray(new String[0]);

      } else {
        if (this.aliases.containsKey(split[0].toLowerCase())) {
          split[0] = this.aliases.get(split[0].toLowerCase());
        }
        if (this.commands.containsKey(split[0].toLowerCase())) {
          Command command = this.commands.get(split[0].toLowerCase());
          return command
              .calculateMissingArguments(sender, commandName, this.removeFirstFromArray(split));
        }
        return nullString;
      }
    } else {
      if (this.aliases.containsKey(commandName)) {
        commandName = this.aliases.get(commandName);
      }
      if (this.commands.containsKey(commandName)) {
        Command command = this.commands.get(commandName);
        return command
            .calculateMissingArguments(sender, commandName, this.removeFirstFromArray(split));
      }
      return nullString;
    }
  }
}

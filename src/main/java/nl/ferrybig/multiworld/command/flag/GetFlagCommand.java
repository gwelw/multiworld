package nl.ferrybig.multiworld.command.flag;

import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.chat.Formatter;
import nl.ferrybig.multiworld.command.ArgumentType;
import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.handler.DataHandler;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.handler.WorldHandler;
import nl.ferrybig.multiworld.exception.InvalidFlagException;
import nl.ferrybig.multiworld.translation.Translation;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GetFlagCommand extends Command {

  private final DataHandler dataHandler;
  private final WorldHandler worldHandler;

  public GetFlagCommand(DataHandler data, WorldHandler worldHandler) {
    super("getflag", "Gets the value of a block");
    this.dataHandler = data;
    this.worldHandler = worldHandler;
  }

  @Override
  public void runCommand(CommandStack stack) {
    String[] split = stack.getArguments();
    if (split.length != 2) {
      stack.sendMessageUsage(stack.getCommandLabel(), ArgumentType.valueOf("getflag"),
          ArgumentType.TARGET_WORLD, ArgumentType.valueOf("<flag>"));
    } else {
      if (split[1].equals("*"))
      {
        stack.sendMessage(MessageType.HIDDEN_SUCCESS, Translation.COMMAND_GETFLAG_SUCCESS);
        for (String txt : this
            .showWorldFlags(this.dataHandler.getWorldManager().getInternalWorld(split[0], true))) {
          stack.sendMessage(MessageType.SUCCESS, txt);
        }
      } else {
        FlagName flag;
        try {
          flag = FlagName.getFlagFromString(split[1]);
          if (worldHandler.isWorldExistingAndSendMessage(split[0], stack)) {
            stack.sendMessage(MessageType.SUCCESS,
                ChatColor.GREEN + flag.toString() + ChatColor.WHITE + " = " + Formatter
                    .printFlag(this.dataHandler.getWorldManager().getFlag(split[0], flag)));
          }
        } catch (InvalidFlagException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }

  @Override
  public String[] calculateMissingArguments(CommandSender sender, String commandName,
      String[] split) {
    if (split.length == 0) {
      return this.calculateMissingArgumentsWorld("");
    } else if (split.length == 1) {
      return this.calculateMissingArgumentsWorld(split[0]);
    } else if (split.length == 2) {
      return this.calculateMissingArgumentsFlagName(split[1]);
    } else {
      return EMPTY_STRING_ARRAY;
    }
  }

  public String[] showWorldFlags(InternalWorld world) {
    FlagName[] flagsNames = FlagName.class.getEnumConstants();
    StringBuilder out = new StringBuilder();
    for (FlagName flag : flagsNames) {
      out.append("#").append(ChatColor.GREEN).append(flag.toString()).append(ChatColor.WHITE)
          .append(" = ")
          .append(Formatter.printFlag(this.dataHandler
              .getWorldManager().getFlag(world.getName(), flag)));
    }
    return out.toString().split("\\#");
  }
}

package nl.ferrybig.multiworld.command.world;

import nl.ferrybig.multiworld.Utils;
import nl.ferrybig.multiworld.command.ArgumentType;
import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.handler.DataHandler;
import nl.ferrybig.multiworld.exception.InvalidWorldGenException;
import nl.ferrybig.multiworld.exception.WorldGenException;
import nl.ferrybig.multiworld.translation.Translation;
import nl.ferrybig.multiworld.translation.message.MessageCache;
import nl.ferrybig.multiworld.generator.WorldGenerator;
import org.bukkit.command.CommandSender;

public class CreateCommand extends Command {

  private final DataHandler dataHandler;

  public CreateCommand(DataHandler dataHandler) {
    super("world.create", "Creates a new world");
    this.dataHandler = dataHandler;
  }

  @Override
  public String[] calculateMissingArguments(CommandSender sender, String commandName,
      String[] split) {
    if (split.length == 0) {
      return EMPTY_STRING_ARRAY;
    } else if (split.length == 1) {
      return EMPTY_STRING_ARRAY;
    } else if (split.length == 2) {
      return this.calculateMissingArgumentsWorldGenerator(split[1]);
    } else {
      return EMPTY_STRING_ARRAY;
    }
  }

  @Override
  public void runCommand(CommandStack stack) {
    String[] args = stack.getArguments();
    if (args.length == 0) {
      stack.sendMessageUsage(stack.getCommandLabel(),
          ArgumentType.valueOf("create"),
          ArgumentType.NEW_WORLD_NAME,
          ArgumentType.valueOf("<generator>:<options>"),
          ArgumentType.valueOf("<seed>"));
    } else {
      if (!Utils.checkWorldName(args[0])) {
        stack.sendMessage(MessageType.ERROR,
            Translation.INVALID_WORLD,
            MessageCache.WORLD.get(args[0]));
        return;
      }
      if (dataHandler.getWorldManager().getInternalWorld(args[0], false) != null) {
        stack.sendMessage(MessageType.ERROR,
            Translation.COMMAND_CREATE_WORLD_EXISTS,
            MessageCache.WORLD.get(args[0]));
        return;
      }
      long seed = (new java.util.Random()).nextLong();
      WorldGenerator env = WorldGenerator.NORMAL;
      String genOptions = "";
      String genName;
      try {
        if (args.length > 1) {

          genName = args[1];
          int index = genName.indexOf(':');
          if (index != -1) {
            genOptions = genName.substring(index + 1);
            genName = genName.substring(0, index);
          }
          env = WorldGenerator.getGenByName(genName);
          if (args.length > 2) {
            try {
              seed = Long.parseLong(args[2]);
            } catch (NumberFormatException e) {
              seed = args[2].hashCode();
            }
          }

        }
      } catch (InvalidWorldGenException ex) {
        String error = "Not found:" + ex.getWrongGen();
        stack.sendMessageBroadcast(
            MessageType.ERROR,
            Translation.COMMAND_CREATE_GET_ERROR,
            MessageCache.custom("%error%", error));
        stack.sendMessage(MessageType.ERROR,
            Translation.COMMAND_CREATE_GET_ERROR,
            MessageCache.custom("%error%", error));
        return;
      }

      stack.sendMessageBroadcast(null,
          Translation.COMMAND_CREATE_START,
          MessageCache.WORLD.get(args[0]),
          MessageCache.GENERATOR.get(env.getName()),
          MessageCache.GENERATOR_OPTION.get(genOptions),
          MessageCache.SEED.get(String.valueOf(seed)));
      try {
        if (this.dataHandler.getWorldManager().makeWorld(args[0], env, seed, genOptions)) {
          this.dataHandler.scheduleSave();
          stack.sendMessage(MessageType.SUCCESS, Translation.COMMAND_CREATE_SUCCES,
              MessageCache.WORLD.get(args[0]));
        }
      } catch (WorldGenException error) {
        stack.sendMessageBroadcast(
            MessageType.ERROR,
            Translation.COMMAND_CREATE_GET_PRE_ERROR,
            MessageCache.custom("%error%", error.getMessage()));
        stack.sendMessage(
            MessageType.ERROR,
            Translation.COMMAND_CREATE_GET_ERROR,
            MessageCache.custom("%error%", error.getMessage()));
      }
    }
  }
}

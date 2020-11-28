package nl.ferrybig.multiworld.command.world;

import nl.ferrybig.multiworld.command.ArgumentType;
import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.data.WorldHandler;
import nl.ferrybig.multiworld.translation.Translation;
import nl.ferrybig.multiworld.translation.message.MessageCache;
import org.bukkit.command.CommandSender;

public class LoadWorldCommand extends Command {

  private final WorldHandler worldHandler;

  public LoadWorldCommand(WorldHandler worldHandler) {
    super("world.load", "Loads a world");
    this.worldHandler = worldHandler;
  }

  @Override
  public String[] calculateMissingArguments(CommandSender sender, String commandName,
      String[] split) {
    if (split.length < 2) {
      return this.calculateMissingArgumentsWorld("");
    } else {
      return EMPTY_STRING_ARRAY;
    }
  }

  @Override
  public void runCommand(CommandStack stack) {
    String[] args = stack.getArguments();
    if (args.length != 1) {
      stack.sendMessageUsage(stack.getCommandLabel(), ArgumentType.valueOf("load"),
          ArgumentType.TARGET_WORLD);
      return;
    }
    String worldName = args[0];
    if (!this.worldHandler.isWorldExistingAndSendMessage(worldName, stack)) {
      return;
    }
    if (this.worldHandler.isWorldLoaded(args[0])) {
      stack.sendMessage(MessageType.ERROR,
          Translation.WORLD_LOADED_ALREADY,
          MessageCache.WORLD.get(worldName));
      return;
    }
    stack.sendMessageBroadcast(null,
        Translation.WORLD_LOADING_START,
        MessageCache.WORLD.get(worldName));
    if (this.worldHandler.loadWorld(worldName) != null) {
      stack.sendMessageBroadcast(MessageType.SUCCESS,
          Translation.WORLD_LOADING_END,
          MessageCache.WORLD.get(worldName));
    } else {
      stack.sendMessageBroadcast(MessageType.ERROR,
          Translation.WORLD_LOADING_FAILED,
          MessageCache.WORLD.get(worldName));
    }
  }
}

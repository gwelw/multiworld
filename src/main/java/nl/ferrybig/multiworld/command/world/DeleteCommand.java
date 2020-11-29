package nl.ferrybig.multiworld.command.world;

import nl.ferrybig.multiworld.command.ArgumentType;
import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.handler.DataHandler;
import nl.ferrybig.multiworld.data.WorldUtils;
import nl.ferrybig.multiworld.translation.Translation;
import nl.ferrybig.multiworld.translation.message.MessageCache;
import org.bukkit.command.CommandSender;

public class DeleteCommand extends Command {

  private final DataHandler dataHandler;

  public DeleteCommand(DataHandler dataHandler) {
    super("world.delete", "Deletes a world from the MultiWorld index");
    this.dataHandler = dataHandler;
  }

  @Override
  public String[] calculateMissingArguments(CommandSender sender, String commandName,
      String[] split) {
    if (split.length == 0) {
      return this.calculateMissingArgumentsWorld("");
    } else if (split.length == 1) {
      return this.calculateMissingArgumentsWorld(split[0]);
    } else {
      return EMPTY_STRING_ARRAY;
    }
  }

  @Override
  public void runCommand(final CommandStack stack) {
    if (stack.getArguments().length != 1) {
      stack.sendMessageUsage(stack.getCommandLabel(), ArgumentType.valueOf("delete"),
          ArgumentType.TARGET_WORLD);
      return;
    }
    WorldUtils manager = dataHandler.getWorldManager();
    String targetWorld = stack.getArguments()[0];
    if (manager.getWorldMeta(targetWorld, false) == null) {
      stack.sendMessage(MessageType.ERROR, Translation.WORLD_NOT_FOUND,
          MessageCache.WORLD.get(targetWorld));
      return;
    }
    if (manager.isWorldLoaded(targetWorld)) {
      dataHandler.getPlugin().pushCommandStack(stack.newStack().setArguments(new String[]
          {
              "unload", targetWorld
          }).build());
      if (manager.isWorldLoaded(targetWorld)) {
        return;// a message is printed in the unload command if the world unload has failed
      }
    }
    stack.sendMessageBroadcast(null,
        Translation.COMMAND_DELETE_START,
        MessageCache.WORLD.get(targetWorld));
    if (manager.deleteWorld(targetWorld)) {
      this.dataHandler.scheduleSave();
      stack.sendMessageBroadcast(MessageType.SUCCESS,
          Translation.COMMAND_DELETE_SUCCESS,
          MessageCache.WORLD.get(targetWorld));
    } else {
      stack.sendMessageBroadcast(MessageType.ERROR,
          Translation.COMMAND_DELETE_FAILED,
          MessageCache.WORLD.get(targetWorld));
    }
  }
}

package nl.ferrybig.multiworld.command.world;

import nl.ferrybig.multiworld.command.ArgumentType;
import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.handler.WorldHandler;
import nl.ferrybig.multiworld.translation.Translation;
import nl.ferrybig.multiworld.translation.message.MessageCache;
import org.bukkit.command.CommandSender;

public class InfoCommand extends Command {

  private final WorldHandler worldHandler;

  public InfoCommand(WorldHandler worldHandler) {
    super("info", "Shows information about a world");
    this.worldHandler = worldHandler;
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
  public void runCommand(CommandStack stack) {
    String worldName;
    worldName = stack.getLocation().getWorld().getName();
    String[] args = stack.getArguments();
    if (args.length != 0) {
      worldName = args[0];
    }
    if (worldName == null) {
      stack.sendMessageUsage(stack.getCommandLabel(), ArgumentType.valueOf("list"),
          ArgumentType.TARGET_WORLD);
      return;
    }
    if (worldHandler.isWorldExistingAndSendMessage(worldName, stack)) {
      InternalWorld worldObj = this.worldHandler.getWorld(worldName, false);
      stack.sendMessage(MessageType.HIDDEN_SUCCESS,
          Translation.COMMAND_INFO_DATA,
          MessageCache.WORLD.get(worldObj.getName()),
          MessageCache.SEED.get(String.valueOf(worldObj.getSeed())),
          MessageCache.GENERATOR.get(worldObj.getFullGeneratorName()),
          MessageCache.custom("%options%", worldObj.getOptions()),
          MessageCache.ENVIOMENT.get(String.valueOf(worldObj.getEnv())),
          MessageCache.DIFFICULTY.get(String.valueOf(worldObj.getDifficulty())));
    }
  }
}

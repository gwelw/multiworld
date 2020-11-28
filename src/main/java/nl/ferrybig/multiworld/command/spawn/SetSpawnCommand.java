package nl.ferrybig.multiworld.command.spawn;

import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.translation.Translation;
import nl.ferrybig.multiworld.translation.message.MessageCache;
import org.bukkit.Location;

public class SetSpawnCommand extends Command {

  public SetSpawnCommand() {
    super("setspawn", "Sets a spawn of a world");
  }

  @Override
  public void runCommand(CommandStack stack) {
    Location l = stack.getLocation();
    if (l == null) {
      stack.sendMessage(MessageType.ERROR, Translation.COMMAND_SPAWN_FAIL_CONSOLE);
      return;
    }
    boolean succes = l.getWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    if (succes) {
      stack.sendMessageBroadcast(MessageType.SUCCESS,
          Translation.COMMAND_SETSPAWN_SUCCESS,
          MessageCache.custom("%x%", String.valueOf(l.getBlockX())),
          MessageCache.custom("%y%", String.valueOf(l.getBlockY())),
          MessageCache.custom("%z%", String.valueOf(l.getBlockZ())),
          MessageCache.WORLD.get(l.getWorld().getName()));
    } else {
      stack.sendMessage(MessageType.ERROR, Translation.COMMAND_SETSPAWN_FAIL);
    }
  }
}

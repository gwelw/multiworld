package nl.ferrybig.multiworld.command.spawn;

import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.translation.Translation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends Command {

  public SpawnCommand() {
    super("spawn", "Teleports yourself to spawn");
  }

  @Override
  public void runCommand(CommandStack stack) {
    CommandSender sender = stack.getSender();
    if (sender instanceof Player) {
      Player player = (Player) sender;
      player.teleport(player.getWorld().getSpawnLocation());
      stack.sendMessage(MessageType.SUCCESS, Translation.COMMAND_SPAWN_SUCCES);
    } else {
      stack.sendMessage(MessageType.ERROR, Translation.COMMAND_SPAWN_FAIL_CONSOLE);
    }
  }
}
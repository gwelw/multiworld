package nl.ferrybig.multiworld.command.world;

import java.util.Arrays;
import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.translation.Translation;
import nl.ferrybig.multiworld.translation.message.MessageCache;
import org.bukkit.ChatColor;

public class ListCommand extends Command {

  private final DataHandler data;

  public ListCommand(DataHandler data) {
    super("list", "Lists al worlds on the server");
    this.data = data;
  }

  @Override
  public void runCommand(CommandStack stack) {
    stack.sendMessage(MessageType.SUCCESS, Translation.COMMAND_LIST_HEADER);
    InternalWorld[] worlds = this.data.getWorldManager().getWorlds(false);
    Arrays.sort(worlds, (t, t1) -> t.getName().compareToIgnoreCase(t1.getName()));

    for (InternalWorld world : worlds) {
      stack.sendMessage(MessageType.HIDDEN_SUCCESS,
          Translation.COMMAND_LIST_DATA,
          MessageCache.WORLD.get(world.getName()),
          MessageCache.custom("%loaded%",
              (this.data.getWorldManager().isWorldLoaded(world.getName()) ? ChatColor.GREEN
                  + "Loaded" : ChatColor.RED + "Unloaded") + RESET),
          MessageCache.custom("%type%", world.getOptions().isEmpty() ? world.getFullGeneratorName()
              : world.getFullGeneratorName() + ":" + world.getOptions())
      );
    }
  }
}

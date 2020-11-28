package nl.ferrybig.multiworld.api;

import nl.ferrybig.multiworld.command.CommandStack;
import org.bukkit.command.CommandSender;

public interface CommandStackBuilder {

  CommandStack build(CommandSender sender);
}

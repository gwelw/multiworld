package nl.ferrybig.multiworld.command;

import java.util.HashMap;
import java.util.Map;
import nl.ferrybig.multiworld.addons.AddonHandler;
import nl.ferrybig.multiworld.command.config.LoadCommand;
import nl.ferrybig.multiworld.command.config.SaveCommand;
import nl.ferrybig.multiworld.command.flag.FlagListCommand;
import nl.ferrybig.multiworld.command.flag.GetFlagCommand;
import nl.ferrybig.multiworld.command.flag.SetFlagCommand;
import nl.ferrybig.multiworld.command.move.GotoCommand;
import nl.ferrybig.multiworld.command.move.MoveCommand;
import nl.ferrybig.multiworld.command.other.DebugCommand;
import nl.ferrybig.multiworld.command.other.EasterEggCommand;
import nl.ferrybig.multiworld.command.other.HelpCommand;
import nl.ferrybig.multiworld.command.plugin.LinkCommand;
import nl.ferrybig.multiworld.command.spawn.SetSpawnCommand;
import nl.ferrybig.multiworld.command.spawn.SpawnCommand;
import nl.ferrybig.multiworld.command.world.CreateCommand;
import nl.ferrybig.multiworld.command.world.DeleteCommand;
import nl.ferrybig.multiworld.command.world.InfoCommand;
import nl.ferrybig.multiworld.command.world.ListCommand;
import nl.ferrybig.multiworld.command.world.LoadWorldCommand;
import nl.ferrybig.multiworld.command.world.UnloadWorldCommand;
import nl.ferrybig.multiworld.command.world.generator.ListWorldGensCommand;
import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.data.PlayerHandler;
import nl.ferrybig.multiworld.data.ReloadHandler;
import nl.ferrybig.multiworld.data.VersionHandler;
import nl.ferrybig.multiworld.data.WorldHandler;

public class CommandHandler extends CommandMap {

  public CommandHandler(DataHandler dataHandler, PlayerHandler playerHandler,
      WorldHandler worldHandler, ReloadHandler reloadHandler, AddonHandler addonHandler,
      VersionHandler versionHandler) {
    super(null, CommandHandler
            .createCommandMap(dataHandler, playerHandler, worldHandler, reloadHandler, addonHandler,
                versionHandler),
        getAliasesMap());
  }

  private static HashMap<String, Command> createCommandMap(DataHandler dataHandler,
      PlayerHandler playerHandler, WorldHandler worldHandler, ReloadHandler reloadHandler,
      AddonHandler addonHandler, VersionHandler versionHandler) {
    HashMap<String, Command> commands = new HashMap<>(30, 0.9f);
    commands.put("help", new HelpCommand(dataHandler));
    commands.put("goto", new GotoCommand(playerHandler, worldHandler));
    commands.put("create", new CreateCommand(dataHandler));
    commands.put("load", new LoadWorldCommand(worldHandler));
    commands.put("unload", new UnloadWorldCommand(worldHandler));
    commands.put("delete", new DeleteCommand(dataHandler));
    commands.put("generators", new ListWorldGensCommand());
    commands.put("move", new MoveCommand(playerHandler, worldHandler));
    commands.put("save", new SaveCommand(reloadHandler));
    commands.put("reloadHandler", new LoadCommand(reloadHandler));
    commands.put("debug", new DebugCommand(versionHandler));
    commands.put("list", new ListCommand(dataHandler));
    commands.put("setflag", new SetFlagCommand(dataHandler, worldHandler));
    commands.put("getflag", new GetFlagCommand(dataHandler, worldHandler));
    commands.put("link", new LinkCommand(dataHandler, worldHandler, addonHandler, false));
    commands.put("link-end", new LinkCommand(dataHandler, worldHandler, addonHandler, true));
    commands.put("snowman", new EasterEggCommand());
    commands.put("flags", new FlagListCommand());
    commands.put("spawn", new SpawnCommand());
    commands.put("setspawn", new SetSpawnCommand());
    commands.put("info", new InfoCommand(worldHandler));

    return commands;
  }

  private static Map<String, String> getAliasesMap() {
    HashMap<String, String> aliases = new HashMap<>(22, 0.9f);
    aliases.put("gens", "listgens");

    aliases.put("link-nether", "link");

    aliases.put("version", "debug");

    aliases.put("world-info", "info");

    aliases.put("flaglist", "flags");
    aliases.put("flag-list", "flags");

    aliases.put("worlds", "list");

    aliases.put("store", "save");

    aliases.put("move-player", "move");

    aliases.put("listgens", "generators");
    aliases.put("list-gens", "generators");

    aliases.put("g", "goto");
    aliases.put("i", "info");
    aliases.put("d", "debug");
    aliases.put("s", "spawn");
    aliases.put("sf", "setflag");
    aliases.put("gf", "getflag");
    aliases.put("ss", "setspawn");
    return aliases;
  }
}

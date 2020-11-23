package nl.ferrybig.multiworld;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import nl.ferrybig.multiworld.addons.AddonHandler;
import nl.ferrybig.multiworld.api.CommandStackBuilder;
import nl.ferrybig.multiworld.api.MultiWorldAPI;
import nl.ferrybig.multiworld.command.CommandHandler;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.DebugLevel;
import nl.ferrybig.multiworld.command.DefaultCommandStack;
import nl.ferrybig.multiworld.command.DefaultMessageLogger;
import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.data.PlayerHandler;
import nl.ferrybig.multiworld.data.ReloadHandler;
import nl.ferrybig.multiworld.data.WorldHandler;
import nl.ferrybig.multiworld.exception.ConfigException;
import nl.ferrybig.multiworld.worldgen.WorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultiWorldPlugin extends JavaPlugin implements CommandStackBuilder {

  private static final Logger log = LoggerFactory.getLogger(MultiWorldPlugin.class);

  private static MultiWorldPlugin instance;
  public CommandStackBuilder builder = this;
  private CommandHandler commandHandler;
  private DataHandler data = null;
  private boolean errorStatus = false;
  private File pluginDir;
  private String version;
  private PlayerHandler playerHandler;
  private AddonHandler pluginHandler;
  private ReloadHandler reloadHandler;
  private WorldHandler worldHandler;

  public static MultiWorldPlugin getInstance() {
    return MultiWorldPlugin.instance;
  }

  @Override
  public CommandStack build(CommandSender sender, DebugLevel level) {
    Location location = null;
    if (sender instanceof Player) {
      location = ((Player) sender).getLocation();
    } else if (sender instanceof BlockCommandSender) {
      location = ((BlockCommandSender) sender).getBlock().getLocation();
    }

    return DefaultCommandStack.builder(new DefaultMessageLogger(level, sender,
        ChatColor.translateAlternateColorCodes('&', "&9[&4MultiWorld&9] &3")))
        .setSender(sender)
        .setLocation(location).setPermissible(sender).build();
  }

  public MultiWorldAPI getApi() {
    if (this.isEnabled()) {
      return new MultiWorldAPI(this);
    }
    return null;
  }

  public CommandHandler getCommandHandler() {
    return commandHandler;
  }

  public DataHandler getDataManager() {
    return this.data;
  }

  @Override
  public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    return WorldGenerator.getGen(id);
  }

  public AddonHandler getPluginHandler() {
    return pluginHandler;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String cmdLine, String[] split) {
    split = Utils.parseArguments(split);
    if (cmd.getName().equals("mw") || cmd.getName().equals("multiworld")) {
      String[] verbose = cmdLine.split("-", 2);
      DebugLevel level;
      if (verbose.length == 2) {
        try {
          level = DebugLevel.valueOf(verbose[1].toUpperCase());
        } catch (Exception ex) {
          level = DebugLevel.NONE;
        }
      } else {
        level = DebugLevel.NONE;
      }
      this.pushCommandStack(this.builder.build(sender, level).editStack().setArguments(split)
          .setCommandLabel(verbose[0]).build());
    } else if (cmd.getName().equals("multiworld-shortcut")) {
      String[] arguments = new String[split.length + 1];
      arguments[0] = cmdLine;
      System.arraycopy(split, 0, arguments, 1, split.length);
      CommandStack stack = this.builder.build(sender, DebugLevel.NONE).editStack()
          .setArguments(arguments).setCommandLabel("multiworld").build();
      this.pushCommandStack(stack);
    }
    return true;
  }

  public void pushCommandStack(CommandStack stack) {
    this.commandHandler.execute(stack);
  }

  @Override
  public void onDisable() {
    if (!this.errorStatus) {
      log.info("Disabled.");
      this.data.onShutdown();
      this.getPluginHandler().disableAll();
    } else {
      log.error("[MultiWorld] !!!     CRITICAL MALL FUNCTION     !!!");
      log.error("[MultiWorld] !!!          SHUTTING DOWN         !!!");
      log.error("[MultiWorld] !!!               :(               !!!");
    }

    Bukkit.getScheduler().cancelTasks(this);
  }

  @Override
  public void onEnable() {
    try {
      MultiWorldPlugin.instance = this;
      PluginDescriptionFile pdfFile = this.getDescription();
      this.version = pdfFile.getVersion();
      this.pluginDir = this.getDataFolder();
      this.pluginDir.mkdir();

      this.data = new DataHandler(this.getConfig(), this);
      this.playerHandler = new PlayerHandler();
      this.worldHandler = new WorldHandler(this.data);
      this.pluginHandler = new AddonHandler(this.data, this.version);
      this.reloadHandler = new ReloadHandler(this.data, this.getPluginHandler());
      this.commandHandler = new CommandHandler(this.data, this.playerHandler, this.worldHandler,
          this.reloadHandler, this.getPluginHandler(), this.getPluginHandler());
      this.pluginHandler.onSettingsChance();
      log.info("v" + this.version + " enabled.");
    } catch (ConfigException e) {
      log.error("[MultiWorld] error while enabling:".concat(e.toString()));
      log.error("[MultiWorld] plz check the configuration for any misplaced tabs, full error:");
      this.errorStatus = true;
      this.setEnabled(false);
    } catch (RuntimeException e) {
      this.getServer().getLogger()
          .log(Level.SEVERE, "[MultiWorld] error while enabling:".concat(e.toString()));
      log.error("[MultiWorld] plz report the full error to the author:");
      this.errorStatus = true;
      this.setEnabled(false);
    }
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, Command command,
      @NotNull String alias, @NotNull String[] split) {
    split = Utils.parseArguments(split);
    return Arrays.asList(
        this.commandHandler.getOptionsForUnfinishedCommands(sender, command.getName(), split));
  }
}

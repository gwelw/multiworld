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
import nl.ferrybig.multiworld.data.MyLogger;
import nl.ferrybig.multiworld.data.PlayerHandler;
import nl.ferrybig.multiworld.data.ReloadHandler;
import nl.ferrybig.multiworld.data.WorldHandler;
import nl.ferrybig.multiworld.exception.ConfigException;
import nl.ferrybig.multiworld.worldgen.SimpleChunkGen;
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

public class MultiWorldPlugin extends JavaPlugin implements CommandStackBuilder {

  private static MultiWorldPlugin instance;
  public CommandStackBuilder builder = this;
  private CommandHandler commandHandler;
  private DataHandler data = null;
  private boolean errorStatus = false;
  private MyLogger log;
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
    Location loc;
    if (sender instanceof Player) {
      loc = ((Player) sender).getLocation();
    } else if (sender instanceof BlockCommandSender) {
      loc = ((BlockCommandSender) sender).getBlock().getLocation();
    } else {
      loc = null;
    }
    return DefaultCommandStack.builder(new DefaultMessageLogger(level, sender,
        ChatColor.translateAlternateColorCodes('&', "&9[&4MultiWorld&9] &3"))).setSender(sender)
        .setLocation(loc).setPermissible(sender).build();
  }

  public void gc() {
    WorldGenerator[] list = WorldGenerator.values();
    for (WorldGenerator w : list) {
      ChunkGenerator gen = WorldGenerator.getGen(w.name());
      if (gen == null) {
        continue;
      }
      if (gen instanceof SimpleChunkGen) {
        ((SimpleChunkGen) gen).gc();
      }
    }
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
    ChunkGenerator gen = WorldGenerator.getGen(id);
    if (gen == null) {
      return null;
    }
    return gen;
  }

  public AddonHandler getPluginHandler() {
    return pluginHandler;
  }

  public void log(String msg) {
    this.log.info(msg);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String cmdLine, String[] split) {
    split = Utils.parseArguments(split);
    if (cmd.getName().equals("mw") || cmd.getName().equals("nl/ferrybig/multiworld")) {
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
    } else if (cmd.getName().equals("nl.ferrybig.multiworld-shortcut")) {
      String[] arguments = new String[split.length + 1];
      arguments[0] = cmdLine;
      System.arraycopy(split, 0, arguments, 1, split.length);
      CommandStack stack = this.builder.build(sender, DebugLevel.NONE).editStack()
          .setArguments(arguments).setCommandLabel("nl/ferrybig/multiworld").build();
      this.pushCommandStack(stack);
    }
    return true;
  }

  public void pushCommandStack(CommandStack stack) {
    this.commandHandler.excute(stack);
  }

  @Override
  public void onDisable() {
    if (!this.errorStatus) {
      this.log.info("Disabled."); //NOI18N
      this.data.onShutdown();
      this.getPluginHandler().disableAll();
    } else {
      this.getServer().getLogger()
          .severe("[MultiWorld] !!!     CRITICAL MALL FUNCTION     !!!"); //NOI18N
      this.getServer().getLogger()
          .severe("[MultiWorld] !!!          SHUTTING DOWN         !!!"); //NOI18N
      this.getServer().getLogger()
          .severe("[MultiWorld] !!!               :(               !!!"); //NOI18N
    }
    this.commandHandler = null;
    this.data = null;
    Bukkit.getScheduler().cancelTasks(this);
    MultiWorldPlugin.instance = null;
  }

  @Override
  public void onEnable() {
    try {
      MultiWorldPlugin.instance = this;
      PluginDescriptionFile pdfFile = this.getDescription();
      this.version = pdfFile.getVersion();
      this.pluginDir = this.getDataFolder();
      this.pluginDir.mkdir();

      this.data = new DataHandler(this.getConfig(), this); //NOI18N
      this.log = this.data.getLogger();
      this.playerHandler = new PlayerHandler(this.data);
      this.worldHandler = new WorldHandler(this.data);
      this.pluginHandler = new AddonHandler(this.data, this.version);
      this.reloadHandler = new ReloadHandler(this.data, this.getPluginHandler());
      this.commandHandler = new CommandHandler(this.data, this.playerHandler, this.worldHandler,
          this.reloadHandler, this.getPluginHandler(), this.getPluginHandler());
      this.pluginHandler.onSettingsChance();
      this.log.info("v" + this.version + " enabled."); //NOI18N
    } catch (ConfigException e) {
      this.getServer().getLogger()
          .log(Level.SEVERE, "[MultiWorld] error while enabling:".concat(e.toString())); //NOI18N
      this.getServer().getLogger().severe(
          "[MultiWorld] plz check the configuration for any misplaced tabs, full error:"); //NOI18N
      e.printStackTrace(System.err);
      this.errorStatus = true;
      this.setEnabled(false);
    } catch (RuntimeException e) {
      this.getServer().getLogger()
          .log(Level.SEVERE, "[MultiWorld] error while enabling:".concat(e.toString())); //NOI18N
      this.getServer().getLogger()
          .severe("[MultiWorld] plz report the full error to the author:"); //NOI18N
      e.printStackTrace(System.err);
      this.errorStatus = true;
      this.setEnabled(false);
    }
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] split) {
    split = Utils.parseArguments(split);
    List<String> list = Arrays.asList(
        this.commandHandler.getOptionsForUnfinishedCommands(sender, command.getName(), split));
    return list;
  }
}

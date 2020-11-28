package nl.ferrybig.multiworld.data;

import nl.ferrybig.multiworld.MultiWorldPlugin;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.data.config.ConfigNode;
import nl.ferrybig.multiworld.data.config.ConfigNodeSection;
import nl.ferrybig.multiworld.data.config.DefaultConfigNode;
import nl.ferrybig.multiworld.data.config.DifficultyConfigNode;
import nl.ferrybig.multiworld.exception.ConfigException;
import nl.ferrybig.multiworld.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataHandler {

  private static final Logger log = LoggerFactory.getLogger(DataHandler.class);

  public static final ConfigNode<ConfigurationSection> OPTIONS_MAIN_NODE = new ConfigNodeSection(
      "options");
  public static final DefaultConfigNode<Boolean> OPTIONS_BLOCK_ENDER_CHESTS = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "blockEnderChestInCrea", false, Boolean.class);
  public static final DefaultConfigNode<Boolean> OPTIONS_LINK_NETHER = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "useportalhandler", false, Boolean.class);
  public static final DefaultConfigNode<Boolean> OPTIONS_LINK_END = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "useEndPortalHandler", false, Boolean.class);
  public static final DefaultConfigNode<Boolean> OPTIONS_WORLD_CHAT = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "useWorldChatSeperator", false, Boolean.class);
  public static final DefaultConfigNode<Boolean> OPTIONS_GAMEMODE = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "usecreativemode", false, Boolean.class);
  public static final DefaultConfigNode<Boolean> OPTIONS_GAMEMODE_INV = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "usecreativemodeinv", true, Boolean.class);
  public static final DefaultConfigNode<Boolean> OPTIONS_WORLD_SPAWN = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "useWorldSpawnHandler", false, Boolean.class);
  public static final DefaultConfigNode<Boolean> OPTIONS_CRAFTBUKKIT_HOOKS = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "craftbukkitHooks", true, Boolean.class);
  public static final DefaultConfigNode<Boolean> OPTIONS_DEBUG = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "debug", false, Boolean.class);
  public static final ConfigNode<Difficulty> OPTIONS_DIFFICULTY = new DifficultyConfigNode(
      OPTIONS_MAIN_NODE, "difficulty", Difficulty.NORMAL);
  public static final DefaultConfigNode<String> OPTIONS_LOCALE = new DefaultConfigNode<>(
      OPTIONS_MAIN_NODE, "locale", "en_US", String.class);

  private final WorldUtils worlds;
  private final MultiWorldPlugin plugin;

  private FileConfiguration config;
  private Difficulty difficulty;
  private boolean unloadWorldsOnDisable = false;
  private SpawnWorldControl spawn;
  private BukkitTask saveTask = null;
  private int configSaveFailed = 0;

  private final Runnable saver = () -> {
    CommandStack console = DataHandler.this.getPlugin().builder.build(Bukkit.getConsoleSender());
    try {
      save();
      console.sendMessageBroadcast(MessageType.SUCCESS, Translation.MULTIWORLD_SAVE_SUCCES);
      configSaveFailed = 0;
    } catch (ConfigException ex) {
      configSaveFailed++;
      if (configSaveFailed < 3) {
        console.sendMessageBroadcast(MessageType.ERROR, Translation.MULTIWORLD_SAVE_FAIL_RETRY);
        scheduleSave(20 * 10);
      } else {
        console.sendMessageBroadcast(MessageType.ERROR, Translation.MULTIWORLD_SAVE_FAIL);
      }
      ex.printStackTrace();
    }
  };

  public DataHandler(FileConfiguration config, MultiWorldPlugin plugin) throws ConfigException {
    this.config = config;
    this.plugin = plugin;
    this.worlds = new WorldManager();
    this.load(true);
  }

  public void scheduleSave() {
    scheduleSave(1200);
  }

  private void scheduleSave(int time) {
    if (this.saveTask == null) {
      this.saveTask = new BukkitRunnable() {
        @Override
        public void run() {
          saver.run();
        }
      }.runTaskLater(plugin, time);
    }
  }

  public WorldUtils getWorldManager() {
    return this.worlds;
  }

  public MultiWorldPlugin getPlugin() {
    return this.plugin;
  }

  public void onShutdown() {
    if (this.saveTask == null) {
      return;
    }
    CommandStack console = this.getPlugin().builder.build(Bukkit.getConsoleSender());
    boolean saved = false;
    this.configSaveFailed = 0;
    while (configSaveFailed < 6 && !saved) {
      try {
        save();
        console.sendMessageBroadcast(MessageType.SUCCESS, Translation.MULTIWORLD_SAVE_SUCCES);
        saved = true;
      } catch (ConfigException ex) {
        console
            .sendMessageBroadcast(MessageType.ERROR, Translation.MULTIWORLD_SAVE_FAIL_RETRY_DIRECT);
        configSaveFailed++;
        ex.printStackTrace();
      }
    }
    if (!saved) {
      console.sendMessageBroadcast(MessageType.ERROR, Translation.MULTIWORLD_SAVE_FAIL_SHUTDOWN);
    }
  }

  public void save() throws ConfigException {
    if (this.saveTask != null) {
      this.saveTask.cancel();
      this.saveTask = null;
    }
    this.config.options().header("# options.debug: must the debug output be printed?\n"
        + "# options.difficulty: what is the server diffeculty?\n"
        + "# options.locale: what set of lang files must be used, supported: en_US, nl_NL, de_DE, it_IT\n"
        + "# spawnGroup: used to set withs worlds have what spawn, difficult to use. see official site for details");
    ConfigurationSection l1;
    l1 = this.config.createSection("worlds");
    this.worlds.saveWorlds(l1, this.spawn);
    if (this.spawn != null) {
      this.spawn.save(config.createSection("spawnGroup"));
    }
    this.plugin.saveConfig();
  }

  public void load() throws ConfigException {
    this.load(false);
  }

  private void load(boolean isStartingUp) throws ConfigException {
    if (!isStartingUp) {
      this.plugin.reloadConfig();
      this.config = this.plugin.getConfig();
    }
    log.debug("config loaded");
    this.difficulty = getNode(OPTIONS_DIFFICULTY);

    /* locale setting */
		/*{
			String tmp1;
			String tmp2 = "";
			String tmp3 = "";
			String[] tmp4 = getNode(OPTIONS_LOCALE).split("_");
			switch (tmp4.length)
			{
				case 3:
					tmp3 = tmp4[2];
				case 2:
					tmp2 = tmp4[1];
				default:
					tmp1 = tmp4[0];
					break;
			}
			@SuppressWarnings("deprecation")
			LangStrings lang1 = new LangStrings(tmp1, tmp2, tmp3, this.plugin);
			this.lang = lang1;
		}*/
    /* addons settings */

    this.getNode(DataHandler.OPTIONS_DEBUG);
    this.getNode(DataHandler.OPTIONS_GAMEMODE);
    this.getNode(DataHandler.OPTIONS_GAMEMODE_INV);
    this.getNode(DataHandler.OPTIONS_BLOCK_ENDER_CHESTS);
    this.getNode(DataHandler.OPTIONS_LINK_END);
    this.getNode(DataHandler.OPTIONS_LINK_NETHER);
    this.getNode(DataHandler.OPTIONS_WORLD_SPAWN);

    if (this.getNode(DataHandler.OPTIONS_WORLD_SPAWN)) {
      ConfigurationSection spawnGroup = this.config.getConfigurationSection("spawnGroup");
      if (spawnGroup == null) {
        this.config.set("spawnGroup.defaultGroup.world", Bukkit.getWorlds().get(0).getName());
      }
      this.spawn = new SpawnWorldControl(spawnGroup);

    }
    ConfigurationSection worldList = this.config.getConfigurationSection("worlds");
    if (worldList != null) {
      worlds.loadWorlds(worldList, this.difficulty, this.spawn);
    }
    this.save();
  }

  @Override
  public String toString() {
    return "DataHandler{"
        + "worlds=" + worlds
        + ", config=" + config
        + ", plugin=" + plugin
        + ", logger=" + log
        + ", difficulty=" + difficulty
        + ", unloadWorldsOnDisable=" + unloadWorldsOnDisable
        + '}';
  }

  /**
   * Sees if a world is known by nl.ferrybig.multiworld
   * <p>
   *
   * @param world
   * @return
   * @deprecated All calls to this methode should be deglated to the world manager
   */
  @Deprecated
  public boolean isWorldExisting(String world) {
    return worlds.isWorldExisting(world);
  }

  public <T> T getNode(ConfigNode<T> input) {

    return input.get(config);
  }

  public <T> void setNode(ConfigNode<T> input, T value) {
    input.set(config, value);
  }

  public SpawnWorldControl getSpawns() {
    return this.spawn;
  }
}

package nl.ferrybig.multiworld.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.addAll;
import static java.util.Comparator.comparing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import nl.ferrybig.multiworld.Utils;
import nl.ferrybig.multiworld.api.MultiWorldWorldData;
import nl.ferrybig.multiworld.api.events.FlagChangeEvent;
import nl.ferrybig.multiworld.api.events.WorldCreateEvent;
import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.data.config.ConfigNode;
import nl.ferrybig.multiworld.data.config.DifficultyConfigNode;
import nl.ferrybig.multiworld.exception.WorldGenException;
import nl.ferrybig.multiworld.flags.FlagMap;
import nl.ferrybig.multiworld.flags.FlagValue;
import nl.ferrybig.multiworld.generator.NullGenerator;
import nl.ferrybig.multiworld.generator.WorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldManager implements WorldUtils {

  private static final Logger log = LoggerFactory.getLogger(WorldManager.class);

  private static final String NETHER_PORTAL = "netherportal";
  private static final String END_PORTAL = "endportal";
  public static final ConfigNode<Difficulty> WORLD_DIFFICULTY = new DifficultyConfigNode(null,
      "difficulty", Difficulty.NORMAL);
  private final Map<String, WorldContainer> worlds;

  public WorldManager() {
    this.worlds = new HashMap<>();
  }

  @Override
  public World getWorld(String name) {
    return this.getWorldMeta(name, true).getBukkitWorld();
  }

  @Override
  public InternalWorld getInternalWorld(String name, boolean mustBeLoaded) {
    if (name == null) {
      throw new IllegalArgumentException("Name may not be null");
    }
    WorldContainer worldContainer = this.getWorldMeta(name, mustBeLoaded);
    if (worldContainer == null) {
      return null;
    }
    if (mustBeLoaded) {
      return worldContainer.isLoaded() ? worldContainer.getWorld() : null;
    } else {
      return worldContainer.getWorld();
    }
  }

  @Override
  public boolean isWorldLoaded(String name) {
    WorldContainer world = this.getWorldMeta(name, false);
    if (world == null) {
      return false;
    }
    return world.isLoaded();
  }

  @Override
  public boolean isWorldExisting(String world) {
    return getWorldMeta(world, false) != null;
  }

  @Override
  public InternalWorld[] getLoadedWorlds() {
    List<World> loadedWorlds = Bukkit.getWorlds();
    int size = loadedWorlds.size();
    InternalWorld[] array = new InternalWorld[size];
    for (int i = 0; i < size; i++) {
      array[i] = getInternalWorld(loadedWorlds.get(i).getName(), true);
    }
    return array;
  }

  @Override
  public WorldContainer[] getWorlds() {
    List<World> loadedWorlds = Bukkit.getWorlds();
    Collection<WorldContainer> registeredWorlds = this.worlds.values();
    Set<WorldContainer> output = new HashSet<>();

    for (World world : loadedWorlds) {
      output.add(this.getWorldMeta(world.getName(), false));
    }
    output.addAll(registeredWorlds);

    int size = output.size();
    WorldContainer[] array = new WorldContainer[size];
    return output.toArray(array);
  }

  @Override
  public InternalWorld[] getWorlds(boolean listOnlyOnlineWorlds) {
    if (listOnlyOnlineWorlds) {
      return this.getLoadedWorlds();
    }
    List<World> loadedWorlds = Bukkit.getWorlds();
    Collection<WorldContainer> registeredWorlds = this.worlds.values();
    Set<InternalWorld> output = new HashSet<>();

    for (World i : loadedWorlds) {
      output.add(this.getInternalWorld(i.getName(), false));
    }
    for (WorldContainer i : registeredWorlds) {
      output.add(i.getWorld());
    }

    int size = output.size();
    InternalWorld[] array = new InternalWorld[size];
    return output.toArray(array);
  }

  @Override
  public void setFlag(String world, FlagName flag, FlagValue value) {
    this.setFlag(world, flag, value, false);
  }

  private void setFlag(String worldName, FlagName flag, FlagValue value, boolean isStartingUp) {
    WorldContainer w = this.getWorldMeta(worldName, false);
    InternalWorld world = w.getWorld();
    world.getFlags().put(flag, value);
    if (w.isLoaded()) {
      if (flag == FlagName.SPAWN_MONSTER) {
        world.getWorld().setSpawnFlags(value.getAsBoolean(), world.getWorld().getAllowAnimals());
      } else if (flag == FlagName.SPAWN_ANIMAL) {
        world.getWorld().setSpawnFlags(world.getWorld().getAllowMonsters(), value.getAsBoolean());
      } else if (flag == FlagName.REMEMBER_SPAWN) {
        world.getWorld().setKeepSpawnInMemory(value.getAsBoolean());
      } else if (flag == FlagName.PVP) {
        world.getWorld().setPVP(value.getAsBoolean());
      } else if (flag == FlagName.SAVE_ON) {
        world.getWorld().setAutoSave(value.getAsBoolean());
      }
    }
    if (!isStartingUp && value != FlagValue.UNKNOWN) {
      new FlagChangeEvent(w, flag, value.getAsBoolean(flag)).call();
    }
  }

  @Override
  public FlagValue getFlag(String worldName, FlagName flag) {
    WorldContainer worldContainer = this.getWorldMeta(worldName, false);
    if (worldContainer.isLoaded()) {
      InternalWorld world = worldContainer.getWorld();
      switch (flag) {
        case SPAWN_MONSTER:
          return FlagValue.fromBoolean(world.getWorld().getAllowMonsters());
        case SPAWN_ANIMAL:
          return FlagValue.fromBoolean(world.getWorld().getAllowAnimals());
        case REMEMBER_SPAWN:
          return FlagValue.fromBoolean(world.getWorld().getKeepSpawnInMemory());
        case CREATIVE_WORLD: {
          FlagValue flagValue = world.getFlags().get(flag);
          if (flagValue == null) {
            return FlagValue
                .fromBoolean(Bukkit.getDefaultGameMode() == org.bukkit.GameMode.CREATIVE);
          }
          return flagValue;
        }
        case SAVE_ON:
          return FlagValue.fromBoolean(world.getWorld().isAutoSave());
        case RECEIVE_CHAT:
        case SEND_CHAT:
          return world.getFlags().getOrDefault(flag, FlagValue.TRUE);
        case PVP:
          return FlagValue.fromBoolean(world.getWorld().getPVP());
        default:
          throw new RuntimeException("Cannot find that flag");
      }
    } else {
      return worldContainer.getWorld().getFlags().getOrDefault(flag, FlagValue.UNKNOWN);
    }
  }

  @Override
  public boolean makeWorld(String name, WorldGenerator env, long seed, String options)
      throws WorldGenException {
    if (this.getWorldMeta(name, false) != null) {
      return false;
    }
    InternalWorld worldData = new InternalWorld()
        .setWorldName(name)
        .setWorldSeed(seed)
        .setWorldType(Environment.NORMAL)
        .setOptions(options)
        .setFlags(new FlagMap())
        .setDifficulty(Difficulty.NORMAL)
        .setFullGeneratorName(env.getName());

    env.makeWorld(worldData);
    if (worldData.getEnv() == null) {
      return false;
    }
    this.createWorld(worldData);
    return true;
  }

  @Override
  public boolean deleteWorld(String world) {
    WorldContainer worldContainer = this.getWorldMeta(world, false);
    if (worldContainer == null) {
      return false;
    }
    if (worldContainer.isLoaded()) {
      return false;
    }
    this.worlds.remove(world.toLowerCase());
    return true;
  }

  @Override
  public boolean unloadWorld(String world) {
    if (Bukkit.unloadWorld(world, true)) {
      this.worlds.get(world.toLowerCase(Locale.ENGLISH)).setLoaded(false);
      return true;
    } else {
      boolean isLoaded = Bukkit.getWorld(world) != null;
      this.worlds.get(world.toLowerCase(Locale.ENGLISH)).setLoaded(isLoaded);
      return !isLoaded;
    }
  }

  @Override
  public World loadWorld(String name) {
    WorldContainer worldContainer = this.worlds.get(name.toLowerCase(Locale.ENGLISH));
    if (worldContainer.isLoaded()) {
      return Bukkit.getWorld(name);
    }
    InternalWorld world = worldContainer.getWorld();
    WorldCreator creator = WorldCreator.name(world.getName()).type(world.getType())
        .seed(world.getSeed()).environment(world.getEnv());
    if (world.getGen() != null) {
      creator = creator.generator(world.getGen());
    }
    World bukkitWorld;
    try {
      bukkitWorld = Bukkit.createWorld(creator);
    } finally {
      worldContainer.setLoaded(Bukkit.getWorld(name) != null);
    }
    if (bukkitWorld == null) {
      return null;
    }
    bukkitWorld.setDifficulty(worldContainer.getWorld().getDifficulty());
    for (Map.Entry<FlagName, FlagValue> next : world.getFlags().entrySet()) {
      if (next.getValue() != FlagValue.UNKNOWN) {
        this.setFlag(name, next.getKey(), next.getValue(), true);
      }
    }
    return bukkitWorld;
  }

  private void createWorld(InternalWorld w) {
    this.addWorld(w, false);
    new WorldCreateEvent(this.getWorldMeta(w.getName(), false)).call();
  }

  @Override
  public WorldContainer getWorldMeta(String world, boolean mustLoad) {
    WorldContainer worldContainer = worlds.get(world.toLowerCase(Locale.ENGLISH));
    if (worldContainer == null) {
      World bukkitWorld = Bukkit.getWorld(world);
      if (bukkitWorld == null) {
        return null;
      }
      ChunkGenerator chunkGenerator = bukkitWorld.getGenerator();
      if (chunkGenerator != null) {
        InternalWorld nullgen = new InternalWorld()
            .setWorldName(bukkitWorld.getName())
            .setWorldSeed(bukkitWorld.getSeed())
            .setFlags(new FlagMap())
            .setFullGeneratorName("NULLGEN")
            .setDifficulty(bukkitWorld.getDifficulty())
            .setType(bukkitWorld.getWorldType())
            .setWorldType(bukkitWorld.getEnvironment())
            .setOptions(chunkGenerator.getClass().getName())
            .setWorldGen(NullGenerator.get());

        worldContainer = this.addWorld(nullgen, true);
      } else {
        InternalWorld internalWorld = new InternalWorld()
            .setWorldName(bukkitWorld.getName())
            .setWorldSeed(bukkitWorld.getSeed())
            .setWorldType(bukkitWorld.getEnvironment())
            .setOptions("")
            .setFlags(new FlagMap())
            .setDifficulty(bukkitWorld.getDifficulty())
            .setFullGeneratorName(bukkitWorld.getEnvironment().name());

        worldContainer = this.addWorld(internalWorld, true);
      }
    }
    return worldContainer;
  }

  private void copyFlags(InternalWorld fromWorld, InternalWorld destinationWorld) {
    for (Map.Entry<FlagName, FlagValue> next : fromWorld.getFlags().entrySet()) {
      this.setFlag(destinationWorld.getName(), next.getKey(), next.getValue(), true);
    }
  }

  @Override
  public boolean setPortal(String fromWorld, String toWorld) {
    checkNotNull(fromWorld, "fromWorld mustn't be null");

    WorldContainer fromWorldContainer = this.getWorldMeta(fromWorld, false);
    WorldContainer toWorldContainer = toWorld == null ? null : getWorldMeta(toWorld, false);

    if (fromWorldContainer == null) {
      return false;
    }
    if (toWorldContainer == null) {
      fromWorldContainer.getWorld().setPortalLink(null);
    } else {
      fromWorldContainer.getWorld().setPortalLink(toWorldContainer.getWorld().getName());
    }
    return true;
  }

  @Override
  public boolean setEndPortal(String fromWorld, String toWorld) {
    checkNotNull(fromWorld, "fromWorld mustn't be null");

    WorldContainer fromWorldContainer = this.getWorldMeta(fromWorld, false);
    WorldContainer toWorldContainer = toWorld == null ? null : getWorldMeta(toWorld, false);

    if (fromWorldContainer == null) {
      return false;
    }
    if (toWorldContainer == null) {
      fromWorldContainer.getWorld().setEndLink("");
    } else {
      fromWorldContainer.getWorld().setEndLink(toWorldContainer.getWorld().getName());
    }
    return true;
  }

  private WorldContainer addWorld(InternalWorld w, boolean isLoaded) {
    WorldContainer world = new WorldContainer(w, isLoaded);
    this.worlds.put(w.getName().toLowerCase(), world);
    return world;
  }

  @Override
  public MultiWorldWorldData[] getAllWorlds() {
    return getWorlds();
  }

  @Override
  public void saveWorlds(ConfigurationSection worldSection, SpawnWorldControl spawnWorldControl) {
    ConfigurationSection l2;
    ConfigurationSection l3;

    Set<WorldContainer> worldContainers = new TreeSet<>(comparing(WorldContainer::getName));
    addAll(worldContainers, getWorlds());

    for (WorldContainer worldContainer : worldContainers) {
      InternalWorld internalWorld = worldContainer.getWorld();
      if (!Utils.checkWorldName(internalWorld.getName())) {
        log.warn("Was not able to save world named: {}", internalWorld.getName());
        continue;
      }
      if (WorldGenerator.NULLGEN.getName().equals(internalWorld.getFullGeneratorName())) {
        continue;
      }
      l2 = worldSection.createSection(internalWorld.getName());
      l2.set("seed", internalWorld.getSeed());
      l2.set("worldgen", internalWorld.getFullGeneratorName());
      l2.set("options", internalWorld.getOptions());
      WORLD_DIFFICULTY.set(l2, internalWorld.getDifficulty());
      l2.set("autoload", worldContainer.isLoaded());
      if (!internalWorld.getFlags().isEmpty()) {
        l3 = l2.createSection("flags");
        for (Map.Entry<FlagName, FlagValue> entry : internalWorld.getFlags().entrySet()) {

          FlagValue get = this.getFlag(internalWorld.getName(), entry.getKey());
          if (get != FlagValue.UNKNOWN) {
            l3.set(entry.getKey().name(), (get == FlagValue.TRUE));
          }
        }
      }

      if (!internalWorld.getPortalWorld().isEmpty()) {
        l2.set(NETHER_PORTAL, internalWorld.getPortalWorld());
      } else {
        l2.set(NETHER_PORTAL, null);
      }
      if (!internalWorld.getEndPortalWorld().isEmpty()) {
        l2.set(END_PORTAL, internalWorld.getEndPortalWorld());
      } else {
        l2.set(END_PORTAL, null);
      }
      if (spawnWorldControl != null) {
        l2.set("spawnGroup", spawnWorldControl.getGroupByWorld(internalWorld.getName()));
      }
    }
  }

  @Override
  public void loadWorlds(ConfigurationSection worlds, Difficulty difficulty,
      SpawnWorldControl spawnWorldControl) {
    for (String worldName : worlds.getValues(false).keySet()) {
      if (worlds.isConfigurationSection(worldName)) {
        try {
          ConfigurationSection world = worlds.getConfigurationSection(worldName);

          long seed = world.getLong("seed", 0L);
          WorldGenerator gen = WorldGenerator
              .valueOf(world.getString("worldgen", "NORMAL").toUpperCase());
          String options = world.getString("options", "");

          FlagMap flags = getFlagMap(world);
          String portal = world.getString(NETHER_PORTAL, "");
          String endPortal = world.getString(END_PORTAL, "");

          InternalWorld worldData = new InternalWorld()
              .setWorldName(worldName)
              .setWorldSeed(seed)
              .setFullGeneratorName(gen.getName())
              .setPortalLink(portal)
              .setEndLink(endPortal)
              .setFlags(flags)
              .setOptions(options)
              .setDifficulty(WORLD_DIFFICULTY.get(world));

          gen.makeWorld(worldData);
          if (worldData.getEnv() == null) {
            continue;
          }

          /* Loads the world at the mem of server */
          this.createWorld(worldData);
          if (world.getBoolean("autoload", true)) {
            this.loadWorld(worldData.getName()).setDifficulty(worldData.getDifficulty());
          }
          if (spawnWorldControl != null) {
            String spawnGroup = world.getString("spawnGroup", "defaultGroup");
            if (!spawnWorldControl.registerWorldSpawn(worldName, spawnGroup)) {
              world.set("spawnGroup", "defaultGroup");
              spawnWorldControl.registerWorldSpawn(worldName, "defaultGroup");
            }
          }
        } catch (IllegalArgumentException e) {
          worlds.set(worldName, null);
          log.warn("Invalid world: {}", worldName);
        } catch (WorldGenException e) {
          worlds.set(worldName, null);
          log.warn("Invalid world gen used for world {} : {}", worldName, e.getMessage());
        } catch (Exception err) {
          log.warn(this.getClass().getName(), "load", err,
              "Some error with '" + worldName + "': " + err.getMessage());
        }
      } else {
        log.warn("{} not a valid world, sorry", worldName);
      }
    }
  }

  @NotNull
  private FlagMap getFlagMap(ConfigurationSection world) {
    FlagMap flags = new FlagMap();
    ConfigurationSection flagList = world.getConfigurationSection("flags");
    if (flagList != null) {
      FlagName[] flagNames = FlagName.class.getEnumConstants();
      for (FlagName flagName : flagNames) {
        if (flagList.isBoolean(flagName.name())) {
          flags.put(flagName, FlagValue.fromBoolean(flagList.getBoolean(flagName.name())));
        }
      }
    }
    return flags;
  }
}

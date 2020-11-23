package nl.ferrybig.multiworld.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import nl.ferrybig.multiworld.Utils;
import nl.ferrybig.multiworld.api.MultiWorldWorldData;
import nl.ferrybig.multiworld.api.events.FlagChanceEvent;
import nl.ferrybig.multiworld.api.events.WorldCreateEvent;
import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.data.config.ConfigNode;
import nl.ferrybig.multiworld.data.config.DifficultyConfigNode;
import nl.ferrybig.multiworld.exception.WorldGenException;
import nl.ferrybig.multiworld.flags.FlagMap;
import nl.ferrybig.multiworld.flags.FlagValue;
import nl.ferrybig.multiworld.worldgen.NullGen;
import nl.ferrybig.multiworld.worldgen.WorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.ChunkGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldManager implements WorldUtils {

  private static final Logger log = LoggerFactory.getLogger(WorldManager.class);
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
      if (flag == FlagName.SPAWNMONSTER) {
        world.getWorld().setSpawnFlags(value.getAsBoolean(), world.getWorld().getAllowAnimals());
      } else if (flag == FlagName.SPAWNANIMAL) {
        world.getWorld().setSpawnFlags(world.getWorld().getAllowMonsters(), value.getAsBoolean());
      } else if (flag == FlagName.REMEMBERSPAWN) {
        world.getWorld().setKeepSpawnInMemory(value.getAsBoolean());
      } else if (flag == FlagName.PVP) {
        world.getWorld().setPVP(value.getAsBoolean());
      } else if (flag == FlagName.SAVEON) {
        world.getWorld().setAutoSave(value.getAsBoolean());
      }
    }
    if (!isStartingUp && value != FlagValue.UNKNOWN) {
      new FlagChanceEvent(w, flag, value.getAsBoolean(flag)).call();
    }
  }

  @Override
  public FlagValue getFlag(String worldName, FlagName flag) {
    WorldContainer worldContainer = this.getWorldMeta(worldName, false);
    if (worldContainer.isLoaded()) {
      InternalWorld world = worldContainer.getWorld();
      switch (flag) {
        case SPAWNMONSTER:
          return FlagValue.fromBoolean(world.getWorld().getAllowMonsters());
        case SPAWNANIMAL:
          return FlagValue.fromBoolean(world.getWorld().getAllowAnimals());
        case REMEMBERSPAWN:
          return FlagValue.fromBoolean(world.getWorld().getKeepSpawnInMemory());
        case CREATIVEWORLD: {
          FlagValue flagValue = world.getFlags().get(flag);
          if (flagValue == null) {
            return FlagValue
                .fromBoolean(Bukkit.getDefaultGameMode() == org.bukkit.GameMode.CREATIVE);
          }
          return flagValue;
        }
        case SAVEON:
          return FlagValue.fromBoolean(world.getWorld().isAutoSave());
        case RECIEVECHAT:
        case SENDCHAT:
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
    InternalWorld worldData = new InternalWorld(name, seed, World.Environment.NORMAL, null, options,
        new FlagMap(), env.name(), null, null, Difficulty.NORMAL, WorldType.NORMAL);
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
        worldContainer = this.addWorld(
            new InternalWorld(bukkitWorld.getName(), bukkitWorld.getSeed(),
                bukkitWorld.getEnvironment(), NullGen.get(),
                chunkGenerator.getClass().getName(), new FlagMap(),
                "NULLGEN", null, null, bukkitWorld.getDifficulty(), bukkitWorld.getWorldType()),
            true);
      } else {
        worldContainer = this.addWorld(
            new InternalWorld(bukkitWorld.getName(), bukkitWorld.getSeed(),
                bukkitWorld.getEnvironment(), null, "",
                new FlagMap(),
                bukkitWorld.getEnvironment().name(), bukkitWorld.getDifficulty()), true);
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
  public void saveWorlds(ConfigurationSection worldSection, SpawnWorldControl spawn) {
    ConfigurationSection l2;
    ConfigurationSection l3;
    for (WorldContainer i : new TreeSet<>(new Comparator<WorldContainer>() {
      @Override
      public int compare(WorldContainer t, WorldContainer t1) {
        return t.getName()
            .compareTo(t1.getName()); // This makes the worlds be saved in the same order every time
      }
    }) {
      private static final long serialVersionUID = 1L;

      {
        addAll(Arrays.asList(getWorlds()));
      }
    }) {
      InternalWorld w = i.getWorld();
      if (!Utils.checkWorldName(w.getName())) {
        log.warn("Was not able to save world named: {}", w.getName());
        continue;
      }
      if (WorldGenerator.NULLGEN.getName().equals(w.getFullGeneratorName())) {
        continue;
      }
      l2 = worldSection.createSection(w.getName());
      l2.set("seed", w.getSeed());
      l2.set("worldgen", w.getFullGeneratorName());
      l2.set("options", w.getOptions());
      WORLD_DIFFICULTY.set(l2, w.getDifficulty());
      l2.set("autoload", i.isLoaded());
      if (!w.getFlags().isEmpty()) {
        l3 = l2.createSection("flags");
        for (Map.Entry<FlagName, FlagValue> i1 : w.getFlags().entrySet()) {

          FlagValue get = this.getFlag(w.getName(), i1.getKey());
          if (get != FlagValue.UNKNOWN) {
            l3.set(i1.getKey().name(), (get == FlagValue.TRUE));
          }
        }
      }
      if (!w.getPortalWorld().isEmpty()) {
        l2.set("netherportal", w.getPortalWorld());
      } else {
        l2.set("netherportal", null);
      }
      if (!w.getEndPortalWorld().isEmpty()) {
        l2.set("endportal", w.getEndPortalWorld());
      } else {
        l2.set("endportal", null);
      }
      if (spawn != null) {
        l2.set("spawnGroup", spawn.getGroupByWorld(w.getName()));
      }
    }
  }

  @Override
  public void loadWorlds(ConfigurationSection worlds, Difficulty difficulty,
      SpawnWorldControl spawn) {
    for (String worldName : worlds.getValues(false).keySet()) {
      if (worlds.isConfigurationSection(worldName)) {
        try {
          ConfigurationSection world = worlds.getConfigurationSection(worldName);

          long seed = world.getLong("seed", 0L);
          WorldGenerator gen = WorldGenerator
              .valueOf(world.getString("worldgen", "NORMAL").toUpperCase());
          String options = world.getString("options", "");

          FlagMap flags = new FlagMap();
          ConfigurationSection flagList = world.getConfigurationSection("flags");
          if (flagList != null) {
            FlagName[] flagNames = FlagName.class.getEnumConstants();
            for (FlagName flagName : flagNames) {
              if (!flagList.isBoolean(flagName.name())) {
                continue;
              }
              flags.put(flagName, FlagValue.fromBoolean(flagList.getBoolean(flagName.name())));
            }
          }
          String portal = world.getString("netherportal", "");
          String endPortal = world.getString("endportal", "");

          InternalWorld worldData = new InternalWorld();
          worldData.setWorldName(worldName);
          worldData.setWorldSeed(seed);
          worldData.setFullGeneratorName(gen.getName());
          worldData.setPortalLink(portal);
          worldData.setEndLink(endPortal);
          worldData.setFlags(flags);
          worldData.setOptions(options);

          Difficulty diff = WORLD_DIFFICULTY.get(world);
          worldData.setDifficulty(diff);

          gen.makeWorld(worldData);
          if (worldData.getEnv() == null) {
            continue;
          }

          /* Loads the world at the mem of server */
          this.createWorld(worldData);
          if (world.getBoolean("autoload", true)) {
            this.loadWorld(worldData.getName()).setDifficulty(worldData.getDifficulty());
          }
          if (spawn != null) {
            String spawnGroup = world.getString("spawnGroup", "defaultGroup");
            if (!spawn.registerWorldSpawn(worldName, spawnGroup)) {
              world.set("spawnGroup", "defaultGroup");
              spawn.registerWorldSpawn(worldName, "defaultGroup");
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
}

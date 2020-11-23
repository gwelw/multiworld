package nl.ferrybig.multiworld.data;

import nl.ferrybig.multiworld.api.MultiWorldWorldData;
import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.exception.WorldGenException;
import nl.ferrybig.multiworld.flags.FlagValue;
import nl.ferrybig.multiworld.worldgen.WorldGenerator;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public interface WorldUtils {

  boolean deleteWorld(String world);

  MultiWorldWorldData[] getAllWorlds();

  FlagValue getFlag(String worldName, FlagName flag);

  InternalWorld getInternalWorld(String name, boolean mustBeLoaded);

  InternalWorld[] getLoadedWorlds();

  World getWorld(String name);

  WorldContainer getWorldMeta(String world, boolean mustLoad);

  InternalWorld[] getWorlds(boolean b);

  boolean isWorldLoaded(String name);

  World loadWorld(String name);

  boolean makeWorld(String name, WorldGenerator env, long seed, String options)
      throws WorldGenException;

  boolean setEndPortal(String fromWorld, String toWorld);

  void setFlag(String world, FlagName flag, FlagValue value);

  boolean setPortal(String fromWorld, String toWorld);

  boolean unloadWorld(String world);

  WorldContainer[] getWorlds();

  void loadWorlds(ConfigurationSection worldList, Difficulty baseDifficulty,
      SpawnWorldControl spawn);

  void saveWorlds(ConfigurationSection worldSection, SpawnWorldControl spawn);

  boolean isWorldExisting(String world);
}

package nl.ferrybig.multiworld.api;

import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.exception.ConfigurationSaveException;
import org.bukkit.World;

public interface MultiWorldWorldData {

  String getName();

  boolean isLoaded();

  long getSeed();

  World.Environment getDimension();

  boolean isOptionSet(FlagName flag);

  boolean getOptionValue(FlagName flag);

  void setOptionValue(FlagName flag, boolean newValue) throws ConfigurationSaveException;

  boolean loadWorld() throws ConfigurationSaveException;

  boolean unloadWorld() throws ConfigurationSaveException;

  String getGeneratorType();

  World getBukkitWorld();
}

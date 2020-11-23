package nl.ferrybig.multiworld.data.config;

import java.util.Locale;
import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class DifficultyConfigNode extends ConfigNode<Difficulty> {

  public DifficultyConfigNode(ConfigNode<ConfigurationSection> parent, String configPath,
      Difficulty defaultValue) {
    super(parent, configPath, defaultValue);
  }

  @Override
  protected Object pack(Difficulty data) {
    return data.name();
  }

  @Override
  protected Difficulty unpack(Object configValue) throws DataPackException {
    try {
      return Difficulty.valueOf(configValue.toString().toUpperCase(Locale.ENGLISH));
    } catch (IllegalArgumentException err) {
      try {
        return getDifficulty(configValue);
      } catch (NumberFormatException e) {
        throw new DataPackException("Incorrect difficulty format");
      }
    }
  }

  @NotNull
  private Difficulty getDifficulty(Object configValue) {
    switch (Integer.parseInt(configValue.toString())) {
      case 0:
        return Difficulty.PEACEFUL;
      case 2:
        return Difficulty.NORMAL;
      case 3:
        return Difficulty.HARD;
      case 1:
      default:
        return Difficulty.EASY;
    }
  }
}

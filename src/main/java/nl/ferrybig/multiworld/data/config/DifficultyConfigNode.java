package nl.ferrybig.multiworld.data.config;

import java.util.Locale;
import org.bukkit.Difficulty;
import org.bukkit.configuration.ConfigurationSection;

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
        switch (Integer.parseInt(configValue.toString())) {
          case 0:
            return Difficulty.PEACEFUL;
          case 1:
            return Difficulty.EASY;
          case 2:
            return Difficulty.NORMAL;
          case 3:
            return Difficulty.HARD;
          default:
            return Difficulty.EASY;
        }
      } catch (NumberFormatException er1) {
        throw new DataPackException("Difficulty is in an illegal format!");
      }
    }
  }
}

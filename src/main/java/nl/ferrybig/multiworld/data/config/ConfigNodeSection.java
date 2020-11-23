package nl.ferrybig.multiworld.data.config;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigNodeSection extends ConfigNode<ConfigurationSection> {

  public ConfigNodeSection(String configPath) {
    this(null, configPath);
  }

  public ConfigNodeSection(ConfigNode<ConfigurationSection> parent, String configPath) {
    super(parent, configPath, null);
  }

  @Override
  protected Object pack(ConfigurationSection data) {
    return data;
  }

  @Override
  protected ConfigurationSection unpack(Object rawConfigValue) {
    return (ConfigurationSection) rawConfigValue;
  }

  @Override
  public ConfigurationSection get(ConfigurationSection from) {
    ConfigurationSection configurationSection = from.getConfigurationSection(configPath);
    if (configurationSection == null) {
      configurationSection = from.createSection(configPath);
    }
    return configurationSection;
  }

  @Override
  public void set(ConfigurationSection to, ConfigurationSection value) {
    to.createSection(configPath);
  }
}

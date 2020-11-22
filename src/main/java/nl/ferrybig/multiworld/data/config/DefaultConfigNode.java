package nl.ferrybig.multiworld.data.config;

import org.bukkit.configuration.ConfigurationSection;

public class DefaultConfigNode<T> extends ConfigNode<T> {

  private final Class<T> type;

  public DefaultConfigNode(String configPath, T defaultValue, Class<T> type) {
    this(null, configPath, defaultValue, type);
  }

  public DefaultConfigNode(ConfigNode<ConfigurationSection> parent, String configPath,
      T defaultValue, Class<T> type) {
    super(parent, configPath, defaultValue);
    this.type = type;
  }

  @Override
  protected T unpack(Object configValue) {
    return this.type.cast(configValue);
  }

  @Override
  protected Object pack(T data) {
    return data;
  }
}

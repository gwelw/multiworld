package nl.ferrybig.multiworld.data.config;

import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConfigNode<T> {

  private static final Logger log = LoggerFactory.getLogger(ConfigNode.class);

  protected final String configPath;
  protected final T defaultValue;
  protected final ConfigNode<ConfigurationSection> parent;

  protected ConfigNode(ConfigNode<ConfigurationSection> parent, String configPath, T defaultValue) {
    if (configPath.isEmpty()) {
      throw new IllegalArgumentException("null configPath");
    }
    this.parent = parent;
    this.configPath = configPath;
    this.defaultValue = defaultValue;
  }

  protected String getFullPath() {
    return (parent == null ? "" : (parent.getFullPath() + ".")) + this.configPath;
  }

  protected void set1(ConfigurationSection to, Object value) {
    to.set(this.configPath, value);
  }

  public T get(ConfigurationSection from) {
    if (this.parent != null) {
      from = this.parent.get(from);
    }
    if (!from.contains(configPath)) {
      log.debug("Adding missing config node: {}", this.getFullPath());
      this.set1(from, pack(defaultValue));
      return defaultValue;
    } else {
      Object get = from.get(configPath, this.pack(defaultValue));

      try {
        return this.unpack(get);
      } catch (DataPackException e) {
        log.warn("Error with node {} fix it, it has been replaced by the default value, cause was:"
            + " {}", this.getFullPath(), e.getMessage());
        this.set1(from, this.pack(defaultValue));
        return defaultValue;
      }
    }

  }

  public String getConfigPath() {
    return configPath;
  }

  public T getDefaultValue() {
    return defaultValue;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    @SuppressWarnings(value = "unchecked") final DefaultConfigNode<?> other = (DefaultConfigNode<?>) object;
    if (!Objects.equals(this.configPath, other.configPath)) {
      return false;
    }
    return Objects.equals(this.defaultValue, other.defaultValue);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + (this.configPath != null ? this.configPath.hashCode() : 0);
    hash = 59 * hash + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
    return hash;
  }

  public void set(ConfigurationSection to, T value) {
    if (this.parent != null) {
      to = this.parent.get(to);
    }
    this.set1(to, this.pack(value));
  }

  @Override
  public String toString() {
    return this.getClass().getName() + "{" + "configPath=" + configPath + ", defaultValue="
        + defaultValue + '}';
  }

  protected abstract T unpack(Object rawConfigValue) throws DataPackException;

  protected abstract Object pack(T data);
}

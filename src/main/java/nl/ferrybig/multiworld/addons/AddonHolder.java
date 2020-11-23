package nl.ferrybig.multiworld.addons;

import java.util.StringJoiner;
import nl.ferrybig.multiworld.MultiWorldPlugin;
import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.data.config.DefaultConfigNode;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddonHolder<T extends MultiworldAddon> implements MultiworldAddon, SettingsListener {

  private static final Logger log = LoggerFactory.getLogger(AddonHolder.class);

  private final DataHandler dataHandler;
  private final Class<T> type;
  private final String name;
  private final DefaultConfigNode<Boolean> updateNode;
  private T addon = null;

  public AddonHolder(Class<T> type, String name, DataHandler dataHandler,
      DefaultConfigNode<Boolean> updateNode) {
    if (type == null || name == null || dataHandler == null) {
      throw new NullPointerException("null not accepted as param");
    }
    this.type = type;
    this.name = name;
    this.dataHandler = dataHandler;
    this.updateNode = updateNode;
  }

  @Override
  public void onDisable() {
    assert getAddon() != null;
    log.debug("Disabling plugin {}", type.getSimpleName());
    this.getAddon().onDisable();
  }

  @Override
  public void onEnable() {
    if (this.getAddon() == null) {
      try {
        this.addon = type.getConstructor(DataHandler.class).newInstance(this.dataHandler);
        assert getAddon() != null;
        log.debug("Loaded plugin {}", type.getSimpleName());
        if (this.getAddon() instanceof Listener) {
          Bukkit.getServer().getPluginManager()
              .registerEvents((Listener) this.getAddon(), MultiWorldPlugin.getInstance());
        }
      } catch (Exception ex) {
        throw new RuntimeException("Mistake from delevoper: " + ex.toString(), ex);
      }
    }
    log.debug("Enabling plugin {}", type.getSimpleName());
    this.getAddon().onEnable();
  }

  @Override
  public void onSettingsChance() {
    if (isEnabledInsideConfig()) {
      if (!this.isEnabled()) {
        this.onEnable();
      }
    } else {
      if (this.isEnabled()) {
        this.onDisable();
      }
    }
  }

  public boolean isLoaded() {
    return this.getAddon() != null;
  }

  @Override
  public boolean isEnabled() {
    if (this.getAddon() == null) {
      return false;
    } else {
      return this.getAddon().isEnabled();
    }
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", AddonHolder.class.getSimpleName() + "[", "]")
        .add("type=" + type)
        .add("name='" + name + "'")
        .add("updateNode=" + updateNode)
        .add("addon=" + addon)
        .toString();
  }

  public T getAddon() {
    return addon;
  }

  boolean isEnabledInsideConfig() {
    return this.dataHandler.getNode(this.updateNode);
  }
}

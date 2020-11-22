package nl.ferrybig.multiworld.api;

import java.util.Objects;
import nl.ferrybig.multiworld.MultiWorldPlugin;
import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.exception.ConfigException;

public class MultiWorldAPI {

  protected final MultiWorldPlugin plugin;

  public MultiWorldAPI(MultiWorldPlugin plugin) {
    this.plugin = plugin;
  }

  protected boolean isValid() {
    if (this.plugin.isEnabled()) {
      return true;
    }
    return false;
  }

  protected void checkValid() {
    if (!isValid()) {
      throw new IllegalStateException("MultiWorld disabled");
    }
  }

  public boolean isCreativeWorld(String world) {

    checkValid();
    return this.getWorld(world).getOptionValue(FlagName.CREATIVEWORLD);
  }

  public boolean isWorldExisting(String world) {
    checkValid();
    return this.getWorld(world) != null;
  }

  public boolean isWorldLoaded(String world) {
    checkValid();
    return this.getWorld(world).isLoaded();
  }

  public MultiWorldWorldData getWorld(String world) {
    checkValid();
    return this.plugin.getDataManager().getWorldManager().getWorldMeta(world, false);
  }

  public MultiWorldWorldData[] getWorlds() {
    checkValid();
    return this.plugin.getDataManager().getWorldManager().getAllWorlds();
  }

  public boolean isLoaded(PluginType addon) {
    checkValid();
    assert this.plugin.getPluginHandler() != null;
    switch (addon) {
      case GAMEMODE_CHANGER:
        return this.plugin.getPluginHandler().isLoaded("GameModeChancer");
      case END_PORTAL_HANDLER:
        return this.plugin.getPluginHandler().isLoaded("EndPortalHandler");
      case NETHER_PORTAL_HANDLER:
        return this.plugin.getPluginHandler().isLoaded("NetherPortalHandler");
      case WORLD_CHAT_SEPARATOR:
        return this.plugin.getPluginHandler().isLoaded("WorldChatSeperatorPlugin");
    }
    return false;
  }

  public boolean isEnabled(PluginType addon) {
    checkValid();
    assert this.plugin.getPluginHandler() != null;
    switch (addon) {
      case GAMEMODE_CHANGER:
        return this.plugin.getPluginHandler().isEnabled("GameModeChancer");
      case END_PORTAL_HANDLER:
        return this.plugin.getPluginHandler().isEnabled("EndPortalHandler");
      case NETHER_PORTAL_HANDLER:
        return this.plugin.getPluginHandler().isEnabled("NetherPortalHandler");
      case WORLD_CHAT_SEPARATOR:
        return this.plugin.getPluginHandler().isEnabled("WorldChatSeperatorPlugin");
    }
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final MultiWorldAPI other = (MultiWorldAPI) obj;
    return Objects.equals(this.plugin, other.plugin);
  }

  @Override
  public int hashCode() {
    return this.plugin != null ? this.plugin.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "MultiWorldAPI{" + "plugin=" + plugin + '}';
  }

  public void saveConfig() throws ConfigurationSaveException {
    checkValid();
    try {
      this.plugin.getDataManager().save();
    } catch (ConfigException ex) {
      throw new ConfigurationSaveException("Error when callig save()", ex);
    }
  }

  public String getCaseCorrectName(String world) {
    MultiWorldWorldData w = this.getWorld(world);
    if (w == null) {
      return world;
    }
    return w.getName();
  }
}

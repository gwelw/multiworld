package nl.ferrybig.multiworld.api;

import java.util.Objects;
import nl.ferrybig.multiworld.MultiWorldPlugin;
import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.exception.ConfigException;
import nl.ferrybig.multiworld.exception.ConfigurationSaveException;

public class MultiWorldApi {

  protected final MultiWorldPlugin plugin;

  public MultiWorldApi(MultiWorldPlugin plugin) {
    this.plugin = plugin;
  }

  protected boolean isValid() {
    return this.plugin.isEnabled();
  }

  protected void checkValid() {
    if (!isValid()) {
      throw new IllegalStateException("MultiWorld disabled");
    }
  }

  public boolean isCreativeWorld(String world) {
    checkValid();
    return this.getWorld(world).getOptionValue(FlagName.CREATIVE_WORLD);
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
        return this.plugin.getPluginHandler().isLoaded("GameModeChanger");
      case END_PORTAL_HANDLER:
        return this.plugin.getPluginHandler().isLoaded("EndPortalHandler");
      case NETHER_PORTAL_HANDLER:
        return this.plugin.getPluginHandler().isLoaded("NetherPortalHandler");
      case WORLD_CHAT_SEPARATOR:
        return this.plugin.getPluginHandler().isLoaded("WorldChatSeparatorPlugin");
    }
    return false;
  }

  public boolean isEnabled(PluginType addon) {
    checkValid();
    assert this.plugin.getPluginHandler() != null;
    switch (addon) {
      case GAMEMODE_CHANGER:
        return this.plugin.getPluginHandler().isEnabled("GameModeChanger");
      case END_PORTAL_HANDLER:
        return this.plugin.getPluginHandler().isEnabled("EndPortalHandler");
      case NETHER_PORTAL_HANDLER:
        return this.plugin.getPluginHandler().isEnabled("NetherPortalHandler");
      case WORLD_CHAT_SEPARATOR:
        return this.plugin.getPluginHandler().isEnabled("WorldChatSeparatorPlugin");
    }
    return false;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    final MultiWorldApi other = (MultiWorldApi) object;
    return Objects.equals(this.plugin, other.plugin);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.plugin);
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

  public String getCaseCorrectName(String worldName) {
    MultiWorldWorldData worldData = this.getWorld(worldName);
    if (worldData == null) {
      return worldName;
    }
    return worldData.getName();
  }
}

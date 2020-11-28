package nl.ferrybig.multiworld.addons;

import static nl.ferrybig.multiworld.data.DataHandler.OPTIONS_BLOCK_ENDER_CHESTS;
import static nl.ferrybig.multiworld.data.DataHandler.OPTIONS_GAMEMODE;
import static nl.ferrybig.multiworld.data.DataHandler.OPTIONS_LINK_END;
import static nl.ferrybig.multiworld.data.DataHandler.OPTIONS_LINK_NETHER;
import static nl.ferrybig.multiworld.data.DataHandler.OPTIONS_WORLD_CHAT;
import static nl.ferrybig.multiworld.data.DataHandler.OPTIONS_WORLD_SPAWN;

import java.util.HashMap;
import java.util.Map;
import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.data.config.DefaultConfigNode;

public class AddonMap implements SettingsListener, PluginList {

  private final DataHandler data;
  private final Map<String, AddonHolder<? extends MultiworldAddon>> pluginMap = new HashMap<>();

  public AddonMap(DataHandler data) {

    this.data = data;
    this.addPlugin(WorldChatSeparatorPlugin.class, "WorldChatSeperatorPlugin",
        OPTIONS_WORLD_CHAT);
    this.addPlugin(NetherPortalHandler.class, "NetherPortalHandler",
        OPTIONS_LINK_NETHER);
    this.addPlugin(EndPortalHandler.class, "EndPortalHandler", OPTIONS_LINK_END);
    this.addPlugin(GameModeAddon.class, "GameModeChancer", OPTIONS_GAMEMODE);
    this.addPlugin(EnderChestBlocker.class, "EnderChestBlocker",
        OPTIONS_BLOCK_ENDER_CHESTS);
    this.addPlugin(WorldSpawnControl.class, "WorldSpawnHandler", OPTIONS_WORLD_SPAWN);
  }

  private <T extends MultiworldAddon> void addPlugin(Class<T> type, String name,
      DefaultConfigNode<Boolean> config) {
    pluginMap.put(name.toUpperCase(), new AddonHolder<>(type, name, data, config));
  }

  public AddonHolder<? extends MultiworldAddon> getPlugin(String plugin) {
    return pluginMap.get(plugin.toUpperCase());
  }

  @Override
  public void onSettingsChance() {
    for (AddonHolder<?> plugin : pluginMap.values()) {
      plugin.onSettingsChance();
    }
  }

  @Override
  public boolean isLoaded(String plugin) {
    return getPlugin(plugin).isLoaded();
  }

  @Override
  public boolean isEnabled(String plugin) {
    return getPlugin(plugin).isEnabled();
  }

  @Override
  public String[] getPlugins() {
    String[] plugins = new String[this.pluginMap.size()];
    int i = 0;
    for (AddonHolder<?> plugin : this.pluginMap.values()) {
      plugins[i] = plugin.getName();
      i++;
    }
    return plugins;
  }

  @Override
  public String toString() {
    return "AddonMap{" + "pluginMap=" + pluginMap + '}';
  }

  @Override
  public void disableAll() {
    this.pluginMap.values().stream()
        .filter(AddonHolder::isEnabled)
        .forEach(AddonHolder::onDisable);
  }

  @Override
  public boolean enabledInsideConfig(String plugin) {
    return getPlugin(plugin).isEnabledInsideConfig();
  }
}

package nl.ferrybig.multiworld.addons;

import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.data.VersionHandler;

public final class AddonHandler implements VersionHandler, SettingsListener {

  private final String version;
  private final AddonMap plugins;

  public AddonHandler(DataHandler dataHandler, String version) {
    this.version = version;
    this.plugins = new AddonMap(dataHandler);
  }

  @Override
  public String getVersion() {
    return this.version;
  }

  @Override
  public boolean isLoaded(String plugin) {
    return plugins.isLoaded(plugin);
  }

  @Override
  public boolean isEnabled(String plugin) {
    return plugins.isEnabled(plugin);
  }

  @Override
  public String[] getPlugins() {
    return plugins.getPlugins();
  }

  public AddonHolder<? extends MultiworldAddon> getPlugin(String plugin) {
    return plugins.getPlugin(plugin);
  }

  @Override
  public void onSettingsChance() {
    this.plugins.onSettingsChance();
  }

  @Override
  public void disableAll() {
    this.plugins.disableAll();
  }

  @Override
  public boolean enabledInsideConfig(String plugin) {
    return this.plugins.enabledInsideConfig(plugin);
  }
}

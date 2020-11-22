package nl.ferrybig.multiworld.addons;

public interface PluginList {

  boolean isLoaded(String plugin);

  boolean isEnabled(String plugin);

  String[] getPlugins();

  void disableAll();

  boolean enabledInsideConfig(String plugin);
}

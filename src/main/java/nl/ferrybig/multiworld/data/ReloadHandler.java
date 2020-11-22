package nl.ferrybig.multiworld.data;

import nl.ferrybig.multiworld.exception.ConfigException;
import nl.ferrybig.multiworld.addons.AddonHandler;

public class ReloadHandler {

  private final DataHandler d;
  private final AddonHandler p;

  public ReloadHandler(DataHandler data, AddonHandler plugins) {
    this.d = data;
    this.p = plugins;
  }

  public boolean reload() {
    try {
      this.d.load();
      this.p.onSettingsChance();
    } catch (ConfigException e) {
      this.d.getLogger()
          .throwing("nl.ferrybig.multiworld.data.ReloadHandler", "reload", e);
      return false;
    }
    return true;
  }

  public boolean save() {
    try {
      this.d.save();
    } catch (ConfigException e) {
      this.d.getLogger().throwing("nl.ferrybig.multiworld.data.ReloadHandler", "save", e);
      return false;
    }
    return true;
  }
}

package nl.ferrybig.multiworld.data;

import nl.ferrybig.multiworld.addons.AddonHandler;
import nl.ferrybig.multiworld.exception.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReloadHandler {

  private static final Logger log = LoggerFactory.getLogger(ReloadHandler.class);

  private final DataHandler dataHandler;
  private final AddonHandler addonHandler;

  public ReloadHandler(DataHandler dataHandler, AddonHandler addonHandler) {
    this.dataHandler = dataHandler;
    this.addonHandler = addonHandler;
  }

  public boolean reload() {
    try {
      this.dataHandler.load();
      this.addonHandler.onSettingsChance();
    } catch (ConfigException e) {
      log.error(e.getMessage());
      return false;
    }
    return true;
  }

  public boolean save() {
    try {
      this.dataHandler.save();
    } catch (ConfigException e) {
      log.error(e.getMessage());
      return false;
    }
    return true;
  }
}

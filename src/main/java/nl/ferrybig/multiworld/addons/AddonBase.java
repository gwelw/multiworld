package nl.ferrybig.multiworld.addons;

import nl.ferrybig.multiworld.data.DataHandler;

public abstract class AddonBase implements MultiworldAddon {

  protected final DataHandler dataHandler;
  private boolean isEnabled;

  public AddonBase(DataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  @Override
  public void onDisable() {
    this.isEnabled = false;
  }

  @Override
  public void onEnable() {
    this.isEnabled = true;
  }

  @Override
  public boolean isEnabled() {
    return this.isEnabled;
  }
}

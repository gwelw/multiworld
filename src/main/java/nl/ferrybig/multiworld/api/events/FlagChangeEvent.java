package nl.ferrybig.multiworld.api.events;

import nl.ferrybig.multiworld.api.MultiWorldWorldData;
import nl.ferrybig.multiworld.api.flag.FlagName;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FlagChangeEvent extends MultiWorldEvent {

  private static final HandlerList handlers = new HandlerList();
  private final MultiWorldWorldData world;
  private final FlagName flag;
  private final boolean newValue;

  public FlagChangeEvent(MultiWorldWorldData world, FlagName flag, boolean newValue) {
    super();
    this.world = world;
    this.flag = flag;
    this.newValue = newValue;
  }

  @NotNull
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public FlagName getFlag() {
    return flag;
  }

  public boolean getNewValue() {
    return newValue;
  }

  public MultiWorldWorldData getWorld() {
    return world;
  }
}

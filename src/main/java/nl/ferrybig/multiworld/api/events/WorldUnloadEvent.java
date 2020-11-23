package nl.ferrybig.multiworld.api.events;

import nl.ferrybig.multiworld.api.MultiWorldWorldData;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WorldUnloadEvent extends WorldEvent {

  private static final HandlerList handlers = new HandlerList();

  public WorldUnloadEvent(MultiWorldWorldData world) {
    super(world);
  }

  @NotNull
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

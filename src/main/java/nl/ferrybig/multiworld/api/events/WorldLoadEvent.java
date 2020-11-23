package nl.ferrybig.multiworld.api.events;

import nl.ferrybig.multiworld.api.MultiWorldWorldData;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WorldLoadEvent extends WorldEvent {

  private static final HandlerList handlers = new HandlerList();

  public WorldLoadEvent(MultiWorldWorldData world) {
    super(world);
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @NotNull
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}

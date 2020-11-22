package nl.ferrybig.multiworld.api.events;

import nl.ferrybig.multiworld.api.MultiWorldWorldData;

public abstract class WorldEvent extends MultiWorldEvent {

  private final MultiWorldWorldData world;

  public WorldEvent(MultiWorldWorldData world) {
    super();
    this.world = world;
  }

  public MultiWorldWorldData getWorld() {
    return world;
  }
}

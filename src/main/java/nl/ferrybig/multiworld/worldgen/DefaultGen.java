package nl.ferrybig.multiworld.worldgen;

import nl.ferrybig.multiworld.data.InternalWorld;
import org.bukkit.World;

public class DefaultGen implements ChunkGen {

  public final World.Environment type;

  public DefaultGen(World.Environment worldType) {
    this.type = worldType;
  }

  public DefaultGen() {
    this.type = World.Environment.NORMAL;
  }

  @Override
  public void makeWorld(InternalWorld w) {
    w.setWorldType(type);
  }
}
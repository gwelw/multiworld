package nl.ferrybig.multiworld.generator;

import nl.ferrybig.multiworld.data.InternalWorld;
import org.bukkit.World;

public class DefaultGenerator implements CustomChunkGenerator {

  public final World.Environment type;

  public DefaultGenerator(World.Environment worldType) {
    this.type = worldType;
  }

  public DefaultGenerator() {
    this.type = World.Environment.NORMAL;
  }

  @Override
  public void makeWorld(InternalWorld w) {
    w.setWorldType(type);
  }
}
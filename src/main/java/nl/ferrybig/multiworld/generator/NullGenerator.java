package nl.ferrybig.multiworld.generator;

import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.generator.util.ChunkMaker;
import org.bukkit.World;

public class NullGenerator extends SimpleChunkGenerator {

  private static final NullGenerator INSTANCE = new NullGenerator();

  public static NullGenerator get() {
    return INSTANCE;
  }

  @Override
  public void makeWorld(InternalWorld options) {
    options.setWorldType(null);
  }

  @Override
  protected ChunkMaker makeChunk(World world) {
    return new ChunkMaker(world);
  }
}

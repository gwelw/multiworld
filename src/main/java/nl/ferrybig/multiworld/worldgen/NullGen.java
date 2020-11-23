package nl.ferrybig.multiworld.worldgen;

import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.worldgen.util.ChunkMaker;
import org.bukkit.World;

public class NullGen extends SimpleChunkGen {

  private static NullGen INSTANCE = new NullGen();

  public static NullGen get() {
    return INSTANCE;
  }

  @Override
  public void makeWorld(InternalWorld options) {
    options.setWorldType(null);
  }

  @Override
  protected ChunkMaker makeChunk(World w) {
    return new ChunkMaker(w);
  }

}

package nl.ferrybig.multiworld.worldgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.InvalidWorldGenOptionsException;
import nl.ferrybig.multiworld.worldgen.util.ChunkMaker;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

public abstract class SimpleChunkGen extends MultiWorldChunkGen implements ChunkGen {

  protected final Map<UUID, ChunkMaker> chunk = new HashMap<>();

  @Override
  public ChunkData generateChunkData(World world,
      Random random,
      int x,
      int z,
      ChunkGenerator.BiomeGrid biomes) {

    ChunkMaker tmp = this.chunk.get(world.getUID());
    if (tmp == null) {
      this.chunk.put(world.getUID(), this.makeChunk(world));
      tmp = this.chunk.get(world.getUID());
    }
    Biome b = this.getBiome();
    if (b != null) {
      for (int xCounter = 0; xCounter < 16; xCounter++) {
        for (int zCounter = 0; zCounter < 16; zCounter++) {
          biomes.setBiome(x, z, b);
        }
      }
    }
    return tmp.toChunkData(this.createChunkData(world));
  }

  protected abstract ChunkMaker makeChunk(World world);

  @Override
  public void makeWorld(InternalWorld world) throws InvalidWorldGenOptionsException {
    world.setWorldGen(this);
  }

  @Override
  public boolean canSpawn(World world, int x, int z) {
    return true;
  }

  public Biome getBiome() {
    return null;
  }
}

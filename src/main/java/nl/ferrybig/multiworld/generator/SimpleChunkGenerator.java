package nl.ferrybig.multiworld.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.InvalidWorldGenOptionsException;
import nl.ferrybig.multiworld.generator.util.ChunkMaker;
import org.bukkit.World;
import org.bukkit.block.Biome;

public abstract class SimpleChunkGenerator extends MultiWorldChunkGenerator implements
    CustomChunkGenerator {

  protected final Map<UUID, ChunkMaker> chunk = new HashMap<>();

  @Override
  public ChunkData generateChunkData(World world, Random random, int x, int z,
      org.bukkit.generator.ChunkGenerator.BiomeGrid biomes) {

    ChunkMaker chunkMaker = this.chunk.get(world.getUID());
    if (chunkMaker == null) {
      this.chunk.put(world.getUID(), this.makeChunk(world));
      chunkMaker = this.chunk.get(world.getUID());
    }
    Biome biome = this.getBiome();
    if (biome != null) {
      for (int xCounter = 0; xCounter < 16; xCounter++) {
        for (int zCounter = 0; zCounter < 16; zCounter++) {
          biomes.setBiome(x, z, biome);
        }
      }
    }
    return chunkMaker.toChunkData(this.createChunkData(world));
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

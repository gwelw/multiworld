package nl.ferrybig.multiworld.worldgen;

import java.util.Random;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.worldgen.util.ChunkMaker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class PixelArtRoomChunkGenerator extends MultiWorldChunkGen {

  @Override
  public Location getFixedSpawnLocation(World world, Random random) {
    return null;
  }

  @Override
  public ChunkData generateChunkData(World world, Random random, int x, int z,
      ChunkGenerator.BiomeGrid biomes) {
    ChunkMaker tmpChunk = new ChunkMaker(world);
    tmpChunk.cuboid(0, 0, 0, 15, 0, 15, Material.BEDROCK);
    tmpChunk.cuboid(0, 1, 0, 15, 1, 15, Material.WHITE_WOOL);
    if (Integer.numberOfTrailingZeros(x) >= 3) {
      tmpChunk.cuboid(0, 1, 0, 0, world.getMaxHeight() - 1, 15, Material.WHITE_WOOL);
    } else if (Integer.numberOfTrailingZeros(x + 1) >= 3) {
      tmpChunk.cuboid(15, 1, 0, 15, world.getMaxHeight() - 1, 15, Material.WHITE_WOOL);
    }
    if (Integer.numberOfTrailingZeros(z) >= 3) {
      tmpChunk.cuboid(0, 1, 0, 15, world.getMaxHeight() - 1, 0, Material.WHITE_WOOL);
    } else if (Integer.numberOfTrailingZeros(z + 1) >= 3) {
      tmpChunk.cuboid(0, 1, 15, 15, world.getMaxHeight() - 1, 15, Material.WHITE_WOOL);
    }
    return tmpChunk.toChunkData(this.createChunkData(world));
  }

  @Override
  public void makeWorld(InternalWorld options) {
    options.setWorldGen(this);
  }
}

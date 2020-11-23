package nl.ferrybig.multiworld.worldgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.InvalidWorldGenOptionsException;
import nl.ferrybig.multiworld.worldgen.util.ChunkMaker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class FlatLandChunkGenerator extends SimpleChunkGen {

  private final Map<String, Byte> heightMap = new HashMap<>();

  @Override
  public Location getFixedSpawnLocation(World world, Random random) {
    return null;
  }

  @Override
  protected ChunkMaker makeChunk(World w) {
    ChunkMaker chunk = new ChunkMaker(w.getMaxHeight());
    int seeLevel = w.getSeaLevel();
    int lowestDirt = seeLevel - 3;
    chunk.cuboid(0, 0, 0, 15, 0, 15, Material.BEDROCK);
    chunk.cuboid(0, 1, 0, 15, lowestDirt - 1, 15, Material.STONE);
    chunk.cuboid(0, lowestDirt, 0, 15, seeLevel - 1, 15, Material.DIRT);
    chunk.cuboid(0, seeLevel, 0, 15, seeLevel, 15, Material.GRASS);
    return chunk;
  }

  @Override
  public void makeWorld(InternalWorld world) throws InvalidWorldGenOptionsException {
    super.makeWorld(world);
    this.heightMap.put(world.getName(), parseOptions(world.getOptions()));

  }

  private byte parseOptions(String options) throws InvalidWorldGenOptionsException {
    if (options.isEmpty()) {
      return 64;
    }
    try {
      byte number = Byte.parseByte(options);
      if (number > 127 || number < 0) {
        throw new InvalidWorldGenOptionsException("Argument must be lower than 128");
      }
      return number;
    } catch (NumberFormatException e) {
      throw new InvalidWorldGenOptionsException(e.getLocalizedMessage());
    }
  }

  protected final byte getHeightByWorldName(String name) {
    return this.heightMap.get(name);
  }
}

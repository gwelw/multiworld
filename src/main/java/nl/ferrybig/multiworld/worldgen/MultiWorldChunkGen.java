package nl.ferrybig.multiworld.worldgen;

import java.util.List;
import nl.ferrybig.multiworld.worldgen.populators.Populators;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public abstract class MultiWorldChunkGen extends ChunkGenerator implements ChunkGen,
    BlockConstants {

  @Override
  public List<BlockPopulator> getDefaultPopulators(World world) {
    List<BlockPopulator> list = super.getDefaultPopulators(world);
    list.add(Populators.MYCELIUM.get());
    list.add(Populators.SNOW.get());
    return list;
  }
}

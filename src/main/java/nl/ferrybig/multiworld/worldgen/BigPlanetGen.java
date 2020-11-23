package nl.ferrybig.multiworld.worldgen;

import com.google.common.collect.Lists;
import java.util.List;
import nl.ferrybig.multiworld.worldgen.populators.Populators;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class BigPlanetGen extends AbstractPlanetGen implements ChunkGen {


  public BigPlanetGen() {
  }

  @Override
  public List<BlockPopulator> getDefaultPopulators(World world) {
    return Lists.newArrayList(Populators.BIG_PLANET.get(), Populators.SNOW.get(),
        Populators.MYCELIUM.get());
  }
}

package nl.ferrybig.multiworld.worldgen;

import static com.google.common.collect.Lists.newArrayList;
import static nl.ferrybig.multiworld.worldgen.populators.Populators.MYCELIUM;
import static nl.ferrybig.multiworld.worldgen.populators.Populators.PLANET;
import static nl.ferrybig.multiworld.worldgen.populators.Populators.SNOW;

import java.util.List;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.jetbrains.annotations.NotNull;

public class SmallPlanetGen extends AbstractPlanetGen implements ChunkGen {

  @NotNull
  @Override
  public List<BlockPopulator> getDefaultPopulators(World world) {
    return newArrayList(PLANET.get(), SNOW.get(), MYCELIUM.get());
  }
}

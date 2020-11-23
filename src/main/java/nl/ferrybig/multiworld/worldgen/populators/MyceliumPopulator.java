package nl.ferrybig.multiworld.worldgen.populators;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class MyceliumPopulator extends SurfacePopulator {

  @Override
  public void chanceBlock(int x, int z, Block block) {

    if ((block.getBiome() == Biome.MUSHROOM_FIELDS) || (block.getBiome()
        == Biome.MUSHROOM_FIELD_SHORE)) {
      if ((block.getType() == Material.GRASS)) {
        block.setType(Material.MYCELIUM);
      }
    }

  }

}

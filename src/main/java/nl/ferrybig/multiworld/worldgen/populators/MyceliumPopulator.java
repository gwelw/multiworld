package nl.ferrybig.multiworld.worldgen.populators;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class MyceliumPopulator extends SurfacePopulator {

  @Override
  public void chanceBlock(int x, int z, Block block) {
    if (isCorrectBlock(block)) {
      block.setType(Material.MYCELIUM);
    }
  }

  private boolean isCorrectBlock(Block block) {
    return (block.getBiome() == Biome.MUSHROOM_FIELDS || block.getBiome()
        == Biome.MUSHROOM_FIELD_SHORE) && block.getType() == Material.GRASS;
  }
}

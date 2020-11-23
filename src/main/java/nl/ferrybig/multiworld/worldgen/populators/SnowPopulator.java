package nl.ferrybig.multiworld.worldgen.populators;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SnowPopulator extends SurfacePopulator {

  @Override
  public void chanceBlock(int x, int z, Block block) {
    if ((block.getBiome() == Biome.TAIGA) || (block.getBiome() == Biome.TAIGA_HILLS)) {
      if ((block.getType() == Material.WATER)) {
        //TODO: check for flowing water
        block.setType(Material.ICE);
      } else if ((block.getType() == Material.LAVA) || (block.getType() == Material.GLOWSTONE)) {
      } else if (block.getType() != Material.AIR) {
        block.getRelative(BlockFace.UP).setType(Material.SNOW);
      }
    }
  }
}

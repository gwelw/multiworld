package nl.ferrybig.multiworld.worldgen.populators;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class PumpkingPopulator extends BlockPopulator {

  @Override
  public void populate(World world, Random random, Chunk chunk) {
    if (random.nextInt(32) != 0) {
      return;
    }
    for (int j = 0; j < 16; j++) {

      int globalX = (chunk.getX() << 4) + random.nextInt(16) + 8;
      int globalY = random.nextInt(128);
      int globalZ = (chunk.getZ() << 4) + random.nextInt(16) + 8;
      for (int i = 0; i < 64; i++) {
        int localX = globalX + random.nextInt(8) - random.nextInt(8);
        int localY = globalY + random.nextInt(4) - random.nextInt(4);
        int localZ = globalZ + random.nextInt(8) - random.nextInt(8);
        if (isAir(world, localX, localY, localZ)) {
          world.getBlockAt(localX, localY, localZ).setType(Material.PUMPKIN);
        }
      }
    }
  }

  private boolean isAir(World world, int localX, int localY, int localZ) {
    return world.getBlockAt(localX, localY, localZ).getType() == Material.AIR
        && world.getBlockAt(localX, localY - 1, localZ).getType() != Material.AIR;
  }
}
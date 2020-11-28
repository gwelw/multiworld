package nl.ferrybig.multiworld.worldgen.populators;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.CAKE;
import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.DIAMOND_BLOCK;
import static org.bukkit.Material.DIRT;
import static org.bukkit.Material.EMERALD_BLOCK;
import static org.bukkit.Material.GOLD_BLOCK;
import static org.bukkit.Material.GOLD_ORE;
import static org.bukkit.Material.GRASS;
import static org.bukkit.Material.IRON_BLOCK;
import static org.bukkit.Material.IRON_ORE;
import static org.bukkit.Material.LAPIS_BLOCK;
import static org.bukkit.Material.LAPIS_ORE;
import static org.bukkit.Material.NETHERRACK;
import static org.bukkit.Material.REDSTONE_ORE;
import static org.bukkit.Material.SAND;
import static org.bukkit.Material.SANDSTONE;
import static org.bukkit.Material.SOUL_SAND;
import static org.bukkit.Material.STONE;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

public class BigPlanetPopulator extends AbstractPlanetPopulator {

  protected static final Material[] ALLOWED_BLOCKS = {STONE, DIRT, COBBLESTONE, SANDSTONE};

  protected static final Material[] TOP_LAYER_BLOCK = {GRASS, NETHERRACK, SOUL_SAND, SAND};

  protected static final Material[] SPECIAL_BLOCKS = {AIR, GOLD_ORE, IRON_ORE, REDSTONE_ORE,
      LAPIS_ORE, CAKE, GOLD_BLOCK, IRON_BLOCK, DIAMOND_BLOCK, LAPIS_BLOCK, EMERALD_BLOCK};

  public static final int MAX_SIZE = 30;
  public static final int MIN_SIZE = 5;
  public static final int MAX_POPULATE_DEPTH = 1;

  private int populateDepth;

  @Override
  public void populate(World world, Random random, Chunk source) {
    if (this.populateDepth > MAX_POPULATE_DEPTH) {
      return;
    }
    if (random.nextInt(8) == 0) {
      try {
        this.populateDepth++;
        int planetX = random.nextInt(16) + (source.getX() << 4);
        int planetY = random.nextInt(world.getSeaLevel()) + (64 - MIN_SIZE);
        int planetZ = random.nextInt(16) + (source.getZ() << 4);
        int size = random.nextInt(MAX_SIZE - MIN_SIZE) + MIN_SIZE;
        if (!(planetY + size > world.getMaxHeight() || planetY - size < 0)) {
          this.makePlanet(world,
              planetX,
              planetY,
              planetZ,
              size,
              TOP_LAYER_BLOCK[random.nextInt(TOP_LAYER_BLOCK.length)],
              ALLOWED_BLOCKS[random.nextInt(ALLOWED_BLOCKS.length)],
              SPECIAL_BLOCKS[random.nextInt(SPECIAL_BLOCKS.length)]);

        }
      } finally {
        this.populateDepth--;
      }
    }
  }
}

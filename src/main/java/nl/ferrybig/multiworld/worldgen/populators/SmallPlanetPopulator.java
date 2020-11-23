package nl.ferrybig.multiworld.worldgen.populators;

import static org.bukkit.Material.AIR;
import static org.bukkit.Material.BRICKS;
import static org.bukkit.Material.CAKE;
import static org.bukkit.Material.CLAY;
import static org.bukkit.Material.COBBLESTONE;
import static org.bukkit.Material.DIAMOND_BLOCK;
import static org.bukkit.Material.DIRT;
import static org.bukkit.Material.EMERALD_BLOCK;
import static org.bukkit.Material.GLASS;
import static org.bukkit.Material.GLOWSTONE;
import static org.bukkit.Material.GOLD_BLOCK;
import static org.bukkit.Material.GOLD_ORE;
import static org.bukkit.Material.GRASS;
import static org.bukkit.Material.IRON_BLOCK;
import static org.bukkit.Material.IRON_ORE;
import static org.bukkit.Material.LAPIS_BLOCK;
import static org.bukkit.Material.LAPIS_ORE;
import static org.bukkit.Material.NETHERRACK;
import static org.bukkit.Material.OAK_LOG;
import static org.bukkit.Material.OAK_PLANKS;
import static org.bukkit.Material.REDSTONE_ORE;
import static org.bukkit.Material.SANDSTONE;
import static org.bukkit.Material.STONE;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

public class SmallPlanetPopulator extends AbstractPlanetPopulator {

  public static final Material[] ALLOWED_BLOCKS = {
      AIR, STONE, DIRT, COBBLESTONE, OAK_PLANKS, NETHERRACK, GLASS, OAK_LOG, BRICKS, CLAY
  };

  public static final Material[] TOP_LAYER_BLOCK = {GRASS, GLOWSTONE, OAK_LOG, GLASS, SANDSTONE};

  public static final Material[] SPECIAL_BLOCKS = {
      AIR, GOLD_ORE, IRON_ORE, REDSTONE_ORE, LAPIS_ORE, CAKE, GOLD_BLOCK, IRON_BLOCK,
      DIAMOND_BLOCK, LAPIS_BLOCK, EMERALD_BLOCK
  };

  public static final int NUMBER_OFF_PLANETS = 4;
  public static final int MAX_SIZE = 7;
  public static final int MIN_SIZE = 2;
  public static final int MAX_POPULATE_DEPTH = 3;

  private int populateDepth;

  @Override
  public void populate(World world, Random random, Chunk source) {
    if (populateDepth > MAX_POPULATE_DEPTH) {
      return;
    }
    populateDepth++;
    int planetX, planetY, planetZ, size;

    for (int i = 0; i < NUMBER_OFF_PLANETS; i++) {

      planetY = random.nextInt(world.getMaxHeight());
      size = random.nextInt(MAX_SIZE - MIN_SIZE) + MIN_SIZE;
      if (planetY + size > world.getMaxHeight() || planetY - size < 0) {
        continue;
        //Reject this planet
      }
      planetX = random.nextInt(16) + (source.getX() << 4);
      planetZ = random.nextInt(16) + (source.getZ() << 4);

      this.makePlanet(world, planetX, planetY, planetZ, size,
          TOP_LAYER_BLOCK[random.nextInt(TOP_LAYER_BLOCK.length)],
          ALLOWED_BLOCKS[random.nextInt(ALLOWED_BLOCKS.length)],
          SPECIAL_BLOCKS[random.nextInt(SPECIAL_BLOCKS.length)]);

    }
    populateDepth--;
  }
}

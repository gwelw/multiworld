package nl.ferrybig.multiworld.worldgen.populators;

import nl.ferrybig.multiworld.worldgen.BlockConstants;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public abstract class AbstractPlanetPopulator extends BlockPopulator implements BlockConstants {

  public static final int[] SQUARES =
      {
          0, 1, 4, 9, 16,
          25, 36, 49, 64, 81, 100,
          121, 144, 169, 196, 225,
          256, 289, 324, 361, 400,
          441, 484, 529, 576, 625,
          676, 729, 784, 841, 900
      };

  /**
   * Makes an planet whit the given options
   *
   * @param loc          The location
   * @param size         The size
   * @param blockTop     The block to use as top layer
   * @param blockDown    The block to use as the base block
   * @param blockSpecial The hidden tressure at the middle
   */
  protected void makePlanet(final Location loc, final int size, final Material blockTop,
      final Material blockDown, final Material blockSpecial) {
    this.makePlanet(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), size,
        blockTop, blockDown, blockSpecial);
  }

  /**
   * Makes an planet whit the given options
   *
   * @param workingWorld The world to genarate at
   * @param planetX      the X cordinate
   * @param planetY      the Y cordinate
   * @param planetZ      the Z cordinate
   * @param size         The size of the plenet
   * @param blockTop     The block to use as top layer
   * @param blockDown    The block to use as base
   * @param blockSpecial The hidden tressure inside
   */
  protected void makePlanet(World workingWorld,
      int planetX,
      int planetY,
      int planetZ,
      int size,
      Material blockTop,
      Material blockDown,
      Material blockSpecial) {
    int mainDistance;
    boolean isTop;
    Block workingBlock;

    int posAX = planetX - size;
    int posAY = planetY - size;
    int posAZ = planetZ - size;
    int posBX = planetX + size;
    int posBY = planetY + size;
    int posBZ = planetZ + size;
    int comparatorSize = SQUARES[size];
    for (int x = posAX; x < posBX; x++) {
      for (int z = posAZ; z < posBZ; z++) {
        mainDistance = SQUARES[Math.abs(x - planetX)] + SQUARES[Math.abs(z - planetZ)];
        if (mainDistance <= comparatorSize) {
          isTop = true;
          for (int y = posBY; y > posAY; y--) {
            if (comparatorSize >= mainDistance + SQUARES[Math.abs(y - planetY)]) {
              workingBlock = workingWorld.getBlockAt(x, y, z);
              if (isTop) {
                workingBlock.setType(blockTop);
                isTop = false;
              } else {
                workingBlock.setType(blockDown);
              }
            }

          }
        }

      }
    }
    workingWorld.getBlockAt(planetX, planetY, planetZ).setType(blockSpecial);
  }
}

package nl.ferrybig.multiworld.worldgen.populators;

import org.bukkit.generator.BlockPopulator;

public enum Populators {
  SNOW(new SnowPopulator()),
  PLANET(new SmallPlanetPopulator()),
  BIG_PLANET(new BigPlanetPopulator()),
  DUNGEON(new DungeonPopulator()),
  MYCELIUM(new MyceliumPopulator());
  protected final BlockPopulator populator;

  Populators(BlockPopulator pop) {
    this.populator = pop;
  }

  public BlockPopulator get() {
    return this.populator;
  }
}

package nl.ferrybig.multiworld.worldgen;

import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.InvalidWorldGenException;
import nl.ferrybig.multiworld.exception.WorldGenException;
import org.bukkit.World;
import org.bukkit.WorldType;

public enum WorldGenerator implements ChunkGen {

  NORMAL(World.Environment.NORMAL, "Normal", "Makes a normal minecraft world", SpeedLevel.NORMAL),
  NETHER(World.Environment.NETHER, "Nether", "Makes a nether world", SpeedLevel.NORMAL),
  THE_END(World.Environment.THE_END, "The_End", "Makes a end world", SpeedLevel.NORMAL),
  END(THE_END, "End", "Alias for 'The_End'", false, SpeedLevel.NORMAL),
  FLATLAND(new FlatLandChunkGenerator(), "FlatLand", "Makes a flatland for creative buildings",
      SpeedLevel.FAST),
  PIXELARTROOM(new PixelArtRoomChunkGenerator(), "PixelArtRoom",
      "Makes a world from wool with big walls for pixelart", SpeedLevel.FAST),
  PLANETS(new SmallPlanetGen(), "Planets", "Makes worlds out of planets", SpeedLevel.NORMAL),
  SOLARSYSTEM(new BigPlanetGen(), "SolarSystem", "Makes a world with a few, big planets",
      SpeedLevel.NORMAL),
  ISLANDS(new FlyingIslandsGenerator(), "Islands", "Flying islands", SpeedLevel.NORMAL),
  NULLGEN(NullGen.get(), "NullGen", "An dummy gen to says that the world is made by another plugin",
      false, SpeedLevel.UNKNOWN),
  PLUGIN(new PluginGen(World.Environment.NORMAL), "Plugin", "Makes the world with another plugin.",
      SpeedLevel.UNKNOWN),
  PLUGIN_NETHER(new PluginGen(World.Environment.NETHER), "Plugin_Nether",
      "Makes the world with another plugin (nether version).", SpeedLevel.UNKNOWN),
  PLUGIN_END(new PluginGen(World.Environment.THE_END), "Plugin_End",
      "Makes the world with another plugin (end version).", SpeedLevel.UNKNOWN),
  OCEAN(new OceanGen(), "Ocean", "Makes a world that is 1 big ocean.", SpeedLevel.FAST),
  DESERT(new DesertGen(), "Desert", "Makes a world that is 1 big desert.", SpeedLevel.FAST),
  EPICCAVES(new ChunkGeneratorEpicCaves(), "EpicCaves", "just realy complex (and laggy) caves",
      SpeedLevel.SLOW),
  EMPTY(new EmptyWorldGenerator(), "Empty", "an empty world", SpeedLevel.FAST),
  AMPLIFIED(new WorldTypeBasedGenerator(WorldType.AMPLIFIED), "Amplified",
      "Uses another generator to generate a amplified world"),
  LARGEBIOMES(new WorldTypeBasedGenerator(WorldType.LARGE_BIOMES), "LargeBiomes",
      "Uses another generator to generate a Large Biomes world"),
  SUPERFLAT(new WorldTypeBasedGenerator(WorldType.FLAT), "SuperFlat",
      "Uses minecraft superflat methode to create a world");

  private static final String NO_DESCRIPTION = "NO DESCRIPTION";

  private final ChunkGen generator;
  private final String name;
  private final String description;
  private final Boolean mayShowUP;
  private final SpeedLevel speed;

  WorldGenerator(ChunkGen gen) {
    this(gen, gen.toString(), NO_DESCRIPTION, true, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(World.Environment type) {
    this(new DefaultGen(type), type.name(), NO_DESCRIPTION, true, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(ChunkGen gen, String name, String description) {
    this(gen, name, description, true, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(World.Environment type, String name, String description) {
    this(new DefaultGen(type), name, description, true, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(ChunkGen gen, String name, String description, SpeedLevel speed) {
    this(gen, name, description, true, speed);
  }

  WorldGenerator(World.Environment type, String name, String description, SpeedLevel speed) {
    this(new DefaultGen(type), name, description, true, speed);
  }

  WorldGenerator(World.Environment type, String name, String description, boolean visable) {
    this(new DefaultGen(type), name, description, visable, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(ChunkGen gen, String name, String description, boolean visable) {
    this(gen, name, description, visable, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(World.Environment type, String name, String description, boolean visable,
      SpeedLevel speed) {
    this(new DefaultGen(type), name, description, visable, speed);
  }

  WorldGenerator(ChunkGen gen, String name, String description, boolean visable,
      SpeedLevel speed) {
    this.generator = gen;
    this.name = name;
    this.description = description;
    this.mayShowUP = visable;
    this.speed = speed;
  }

  public static org.bukkit.generator.ChunkGenerator getGen(String id) {
    try {
      WorldGenerator gen = WorldGenerator.valueOf(WorldGenerator.class, id.toUpperCase());
      if (gen.generator instanceof org.bukkit.generator.ChunkGenerator) {
        return (org.bukkit.generator.ChunkGenerator) gen.generator;
      }
    } catch (IllegalArgumentException e) {
    }
    return null;
  }

  public static WorldGenerator getGenByName(String gen)
      throws InvalidWorldGenException {
    try {
      return WorldGenerator.valueOf(gen.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw (InvalidWorldGenException) new InvalidWorldGenException(gen).initCause(e);
    }
  }

  public static String[] getAllGenerators() {
    WorldGenerator[] gens = WorldGenerator.class.getEnumConstants();
    StringBuilder out = new StringBuilder();
    for (WorldGenerator gen : gens) {
      if (gen.mayInList()) {
        out.append(gen.getName()).append(" - ").append(gen.getDescription()).append("#");
      }
    }
    return out.toString().split("#");
  }

  @Override
  public void makeWorld(InternalWorld options) throws WorldGenException {
    this.generator.makeWorld(options);
  }

  public boolean mayInList() {
    return this.mayShowUP;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public SpeedLevel getSpeed() {
    return speed;
  }
}
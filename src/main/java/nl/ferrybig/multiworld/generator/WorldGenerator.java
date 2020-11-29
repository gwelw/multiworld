package nl.ferrybig.multiworld.generator;

import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.InvalidWorldGenException;
import nl.ferrybig.multiworld.exception.WorldGenException;
import org.bukkit.World;
import org.bukkit.WorldType;

public enum WorldGenerator implements CustomChunkGenerator {

  NORMAL(World.Environment.NORMAL, "Normal", "Makes a normal minecraft world", SpeedLevel.NORMAL),
  NETHER(World.Environment.NETHER, "Nether", "Makes a nether world", SpeedLevel.NORMAL),
  THE_END(World.Environment.THE_END, "The_End", "Makes a end world", SpeedLevel.NORMAL),
  END(THE_END, "End", "Alias for 'The_End'", false, SpeedLevel.NORMAL),
  NULLGEN(NullGenerator.get(), "NullGen", "An dummy gen to says that the world is made by another plugin",
      false, SpeedLevel.UNKNOWN),
  PLUGIN(new PluginGenerator(World.Environment.NORMAL), "Plugin", "Makes the world with another plugin.",
      SpeedLevel.UNKNOWN),
  PLUGIN_NETHER(new PluginGenerator(World.Environment.NETHER), "Plugin_Nether",
      "Makes the world with another plugin (nether version).", SpeedLevel.UNKNOWN),
  PLUGIN_END(new PluginGenerator(World.Environment.THE_END), "Plugin_End",
      "Makes the world with another plugin (end version).", SpeedLevel.UNKNOWN),
  EMPTY(new EmptyWorldGenerator(), "Empty", "an empty world", SpeedLevel.FAST),
  AMPLIFIED(new WorldTypeBasedGenerator(WorldType.AMPLIFIED), "Amplified",
      "Uses another generator to generate a amplified world"),
  LARGEBIOMES(new WorldTypeBasedGenerator(WorldType.LARGE_BIOMES), "LargeBiomes",
      "Uses another generator to generate a Large Biomes world"),
  SUPERFLAT(new WorldTypeBasedGenerator(WorldType.FLAT), "SuperFlat",
      "Uses minecraft superflat methode to create a world");

  private static final String NO_DESCRIPTION = "NO DESCRIPTION";

  private final CustomChunkGenerator generator;
  private final String name;
  private final String description;
  private final Boolean mayShowUp;
  private final SpeedLevel speed;

  WorldGenerator(CustomChunkGenerator gen) {
    this(gen, gen.toString(), NO_DESCRIPTION, true, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(World.Environment type) {
    this(new DefaultGenerator(type), type.name(), NO_DESCRIPTION, true, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(CustomChunkGenerator gen, String name, String description) {
    this(gen, name, description, true, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(World.Environment type, String name, String description) {
    this(new DefaultGenerator(type), name, description, true, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(CustomChunkGenerator gen, String name, String description, SpeedLevel speed) {
    this(gen, name, description, true, speed);
  }

  WorldGenerator(World.Environment type, String name, String description, SpeedLevel speed) {
    this(new DefaultGenerator(type), name, description, true, speed);
  }

  WorldGenerator(World.Environment type, String name, String description, boolean visable) {
    this(new DefaultGenerator(type), name, description, visable, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(CustomChunkGenerator gen, String name, String description, boolean visable) {
    this(gen, name, description, visable, SpeedLevel.UNKNOWN);
  }

  WorldGenerator(World.Environment type, String name, String description, boolean visable,
      SpeedLevel speed) {
    this(new DefaultGenerator(type), name, description, visable, speed);
  }

  WorldGenerator(CustomChunkGenerator gen, String name, String description, boolean visable,
      SpeedLevel speed) {
    this.generator = gen;
    this.name = name;
    this.description = description;
    this.mayShowUp = visable;
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
    return this.mayShowUp;
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
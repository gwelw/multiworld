package nl.ferrybig.multiworld.generator;

import java.util.Locale;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.WorldGenException;
import org.bukkit.WorldType;

public class WorldTypeBasedGenerator extends MultiWorldChunkGenerator {

  private final WorldType type;

  public WorldTypeBasedGenerator(WorldType type) {
    this.type = type;
  }

  @Override
  public void makeWorld(InternalWorld options) throws WorldGenException {
    String generator = options.getOptions();
    options.setWorldGen(null);
    String option;
    int index = generator.indexOf(':');
    if (index != -1) {
      option = generator.substring(index + 1);
      generator = generator.substring(0, index);
    } else {
      option = "";
    }

    generator = generator.isBlank() ? "NORMAL" : generator.toUpperCase(Locale.ENGLISH);
    CustomChunkGenerator env = WorldGenerator.getGenByName(generator);

    String originalOptions = options.getOptions();
    options.setOptions(option);
    env.makeWorld(options);
    options.setOptions(originalOptions);

    options.setType(type);
  }
}

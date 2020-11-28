package nl.ferrybig.multiworld.worldgen;

import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.InvalidWorldGenOptionsException;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World.Environment;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginGen extends MultiWorldChunkGen {

  private static final Logger log = LoggerFactory.getLogger(PluginGen.class);

  private final Environment environment;

  public PluginGen(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void makeWorld(InternalWorld options) throws InvalidWorldGenOptionsException {
    String genId = "";
    String pluginName = options.getOptions();
    int index = pluginName.indexOf(':');
    if (index != -1) {
      genId = pluginName.substring(index + 1);
      pluginName = pluginName.substring(0, index);
    }
    if (pluginName.isBlank()) {
      throw new InvalidWorldGenOptionsException("You need to specify a plugin name");
    }
    Server server = Bukkit.getServer();
    PluginManager pluginManager = server.getPluginManager();
    Plugin plugin = pluginManager.getPlugin(pluginName);
    if (plugin == null) {
      throw new InvalidWorldGenOptionsException("Unknown plugin");
    }

    try {
      final ChunkGenerator generator = plugin.getDefaultWorldGenerator(options.getName(), genId);
      if (generator == null) {
        throw new InvalidWorldGenOptionsException(
            "Was not able to find the worldgenenerator with id '" + genId + "' at plugin '" + plugin
                .getDescription().getFullName() + "'");
      }
      options.setWorldType(environment);
      options.setWorldGen(generator);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw (InvalidWorldGenOptionsException) (new InvalidWorldGenOptionsException(
          "Error with custom plugin generator")).initCause(e);
    }
  }
}

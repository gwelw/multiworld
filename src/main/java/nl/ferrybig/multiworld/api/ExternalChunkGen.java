package nl.ferrybig.multiworld.api;

import org.bukkit.World.Environment;
import org.bukkit.generator.ChunkGenerator;

public interface ExternalChunkGen {

  String getDescription();

  String getName();

  ChunkGenerator getGen();

  Environment getEnvoiment();
}

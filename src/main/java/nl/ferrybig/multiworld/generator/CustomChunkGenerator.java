package nl.ferrybig.multiworld.generator;

import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.WorldGenException;

public interface CustomChunkGenerator {

  void makeWorld(InternalWorld options) throws WorldGenException;
}
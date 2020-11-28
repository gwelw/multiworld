package nl.ferrybig.multiworld.worldgen;

import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.WorldGenException;

public interface ChunkGen {

  void makeWorld(InternalWorld options) throws WorldGenException;
}
package nl.ferrybig.multiworld.worldgen;

import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.WorldGenException;

/**
 * The interface that all the world genarators implement
 *
 * @author Fernando
 */
public interface ChunkGen {

  /**
   * return an world whit updates values to make an world whit this gen
   *
   * @param options The options this world have
   * @throws WorldGenException
   */
  void makeWorld(InternalWorld options) throws WorldGenException;
}
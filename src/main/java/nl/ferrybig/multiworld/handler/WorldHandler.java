package nl.ferrybig.multiworld.handler;

import static nl.ferrybig.multiworld.translation.message.MessageCache.WORLD;

import nl.ferrybig.multiworld.Utils;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.data.WorldUtils;
import nl.ferrybig.multiworld.exception.WorldGenException;
import nl.ferrybig.multiworld.translation.Translation;
import nl.ferrybig.multiworld.generator.WorldGenerator;
import org.bukkit.World;

public class WorldHandler {

  private final DataHandler dataHandler;
  private final WorldUtils worlds;

  public WorldHandler(DataHandler dataHandler) {
    this.dataHandler = dataHandler;
    this.worlds = this.dataHandler.getWorldManager();
  }

  public InternalWorld getWorld(String name, boolean mustBeLoaded) {
    if (!Utils.checkWorldName(name)) {
      return null;
    }
    return this.worlds.getInternalWorld(name, mustBeLoaded);
  }

  public boolean isWorldExisting(String world) {
    return this.dataHandler.getWorldManager().isWorldExisting(world);
  }

  public boolean isWorldExistingAndSendMessage(String world, CommandStack stack) {
    if (this.isWorldExisting(world)) {
      return true;
    }

    stack.sendMessage(MessageType.ERROR, Translation.WORLD_NOT_FOUND, WORLD.get(world));
    return false;
  }

  public boolean isWorldLoaded(String world) {
    return this.worlds.isWorldLoaded(world);
  }

  public boolean makeWorld(String name, WorldGenerator env, long seed, String options)
      throws WorldGenException {
    dataHandler.scheduleSave();
    return this.worlds.makeWorld(name, env, seed, options);
  }

  public boolean deleteWorld(String world) {
    dataHandler.scheduleSave();
    return this.worlds.deleteWorld(world);
  }

  public boolean unloadWorld(String world) {
    dataHandler.scheduleSave();
    return this.worlds.unloadWorld(world);
  }

  public World loadWorld(String name) {
    this.dataHandler.scheduleSave();
    return this.worlds.loadWorld(name);
  }
}
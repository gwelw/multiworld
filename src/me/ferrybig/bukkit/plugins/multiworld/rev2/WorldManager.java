/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.bukkit.plugins.multiworld.rev2;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

/**
 *
 * @author Fernando
 */
public interface WorldManager {

    public File getWorldDirectory();

    public WorldDefinition getWorld(UUID uuid);

    public void registerNewGenerator(WorldGenerator gen);

    public Collection<? extends WorldDefinition> getWorlds();
}

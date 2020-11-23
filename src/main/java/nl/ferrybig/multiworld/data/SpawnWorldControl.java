package nl.ferrybig.multiworld.data;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnWorldControl {

  private static final Logger log = LoggerFactory.getLogger(SpawnWorldControl.class);

  private final Map<String, String> groupToWorldSpawn = new HashMap<>();
  private final Map<String, String> worldsToGroupSpawn = new HashMap<>();

  public SpawnWorldControl(ConfigurationSection spawnGroups) {
    groupToWorldSpawn.put("defaultGroup", Bukkit.getWorlds().get(0).getName());
    if (spawnGroups != null) {
      for (String name : spawnGroups.getKeys(false)) {
        String spawnWorld = spawnGroups.getString(name + ".spawn");
        if (spawnWorld != null) {
          groupToWorldSpawn.put(name, spawnWorld);
        }
      }
    }
  }

  public World resolveWorld(String worldFrom) {
    String spawnGroup = worldsToGroupSpawn.get(worldFrom.toUpperCase());
    if (spawnGroup == null) {
      spawnGroup = "defaultGroup";
      registerWorldSpawn(worldFrom, "defaultGroup");
    }
    String targetWorld = groupToWorldSpawn.get(spawnGroup);
    if (targetWorld == null) {
      groupToWorldSpawn.put(spawnGroup, Bukkit.getWorlds().get(0).getName());
      targetWorld = groupToWorldSpawn.get(spawnGroup);
      log.warn("Config error, invalid spawnGroup defined for world {}", worldFrom);
    }
    return Bukkit.getWorld(targetWorld);

  }

  public boolean registerWorldSpawn(String worldName, String spawnGroup) {
    if (groupToWorldSpawn.containsKey(spawnGroup)) {
      worldsToGroupSpawn.put(worldName.toUpperCase(), spawnGroup);
      return true;
    }

    return false;
  }

  public String getGroupByWorld(String name) {
    String spawnGroup = worldsToGroupSpawn.get(name.toUpperCase());
    if (spawnGroup == null) {
      spawnGroup = "defaultGroup";
      registerWorldSpawn(name, "defaultGroup");
    }
    return spawnGroup;
  }

  public void save(ConfigurationSection to) {
    groupToWorldSpawn.forEach((key, value) -> to.set(key + ".spawn", value));
  }
}

package nl.ferrybig.multiworld.addons;

import nl.ferrybig.multiworld.data.DataHandler;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldSpawnControl extends AddonBase implements MultiworldAddon, Listener {

  private static final Logger log = LoggerFactory.getLogger(WorldSpawnControl.class);

  public WorldSpawnControl(DataHandler data) {
    super(data);
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onRespawn(PlayerRespawnEvent event) {
    log.debug("Got player respawn event");
    if (!isEnabled()) {
      return;
    }
    assert this.dataHandler.getSpawns()
        != null; // This class may not be initized when the spawn if turned to false
    World to = this.dataHandler.getSpawns().resolveWorld(event.getPlayer().getWorld().getName());
    log.debug("Chanced spawn of player {} to world {}", event.getPlayer().getName(), to.getName());
    event.setRespawnLocation(to.getSpawnLocation());
  }
}

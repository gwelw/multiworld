package nl.ferrybig.multiworld.addons;

import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.data.InternalWorld;
import nl.ferrybig.multiworld.exception.ConfigException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PortalHandler implements Listener, MultiworldAddon, SettingsListener {

  public static final int END_PORTAL = 1;
  public static final int UNKNOWN_PORTAL = 0;
  public static final int NETHER_PORTAL = -1;
  private final DataHandler data;
  private final Logger log = LoggerFactory.getLogger(PortalHandler.class);
  private final boolean handleEndPortals;
  private boolean enabled = false;

  public PortalHandler(DataHandler d, boolean handleEndPortals) {
    this.data = d;
    this.handleEndPortals = handleEndPortals;
  }

  @Override
  public void onSettingsChance() {
    if (this.enabled) {
      this.load();
    }
  }

  public void load() {
    if (!enabled) {
      throw new IllegalStateException();
    }

    this.log.info("[PortalHandler] loaded!");
  }

  /**
   * Save the data
   *
   * @throws ConfigException       When there was a configuration error
   * @throws IllegalStateException When it wasn't enabled
   */
  public void save() throws ConfigException {
    if (!enabled) {
      throw new IllegalStateException();
    }
    this.data.save();
    this.log.info("[PortalHandler] saved!");
  }
  /*
   * Is this enabled
   */

  /**
   * Checks if this lugin is enabled
   *
   * @return true if its been enabled, false otherwise
   */
  public boolean isEnabled() {
    return this.enabled;
  }

  /**
   * Disable this plugin
   */
  @Override
  public void onDisable() {
    if (!enabled) {
      throw new IllegalStateException();
    }
    enabled = false;
  }

  /**
   * Enable this plugin
   */
  @Override
  public void onEnable() {
    if (enabled) {
      throw new IllegalStateException("Already loaded");
    }
    this.enabled = true;
    this.load();
  }

  /**
   * Adds a link to the database, or remove it if world2 is null
   *
   * @param world1 the target world
   * @param world2 the destination world
   */
  public void add(String world1, String world2) {
    if (this.handleEndPortals) {
      this.data.getWorldManager().setEndPortal(world1, world2);
    } else {
      this.data.getWorldManager().setPortal(world1, world2);
    }
  }

  /**
   * Called when a player uses a portal
   *
   * @param event The event data
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerPortal(EntityPortalEvent event) {
    if (event.isCancelled() || !this.enabled) {
      return;
    }
    int mapType = this.getPortalType(event.getFrom());
    log.debug(
        "[PortalHandler] got portal " + mapType + " for location " + event.getFrom().toVector()
            .toString() + ".");
    if (this.handleEndPortals) {
      if ((mapType == END_PORTAL)) {
        log.debug("[PortalHandler] got PortalType.END.");
        InternalWorld from = this.data.getWorldManager()
            .getInternalWorld(event.getFrom().getWorld().getName(), true);
        String toWorldString = from.getEndPortalWorld();
        if (!toWorldString.isEmpty()) {
          World toWorld = this.data.getWorldManager().getWorld(toWorldString);
          if (toWorld != null) {
            World.Environment toDim = toWorld.getEnvironment(), fromDim = event.getFrom().getWorld()
                .getEnvironment();
            if (toDim == World.Environment.THE_END) {
              Location loc = new Location(toWorld, 100, 54, 0);
              loc = event.getPortalTravelAgent().findOrCreate(loc);
              event.setTo(loc);

            } else {
              Entity ent = event.getEntity();
              Location loc = null;
              if (ent instanceof Player) {
                Player player = (Player) ent;
                loc = player.getBedSpawnLocation();
              }
              if (loc == null || (!loc.getWorld().equals(toWorld))) {
                loc = toWorld.getSpawnLocation();
              }
              loc = event.getPortalTravelAgent().findOrCreate(loc);
              event.setTo(loc);

            }
          }
        }
        log.debug(
            "[PortalHandler] [PortalType.END] used for entity " + event.getEntityType().toString()
                .toLowerCase() + " to get to world " + toWorldString);
      }
    } else {
      if ((!this.handleEndPortals) && (mapType == NETHER_PORTAL)) {
        log.debug("[PortalHandler] got PortalType.NETHER.");
        String toWorldString = this.data.getWorldManager()
            .getInternalWorld(event.getFrom().getWorld().getName(), true).getPortalWorld();
        if (!toWorldString.isEmpty()) {
          World toWorld = this.data.getWorldManager().getWorld(toWorldString);
          if (toWorld != null) {
            World.Environment toDim = toWorld.getEnvironment(), fromDim = event.getFrom().getWorld()
                .getEnvironment();
            float div;
            if (fromDim == toDim)//Env is same at both worlds
            {
              div = 1.0f;
            } else if (fromDim == World.Environment.NETHER) //env is nether at target world
            {
              div = 8.0f;
            } else if (toDim == World.Environment.NETHER) // env is nether at from world
            {
              div = 0.125f;
            } else {
              div = 1.0f;
            }
            Location to = new Location(toWorld, event.getFrom().getX() * div,
                event.getFrom().getY(), event.getFrom().getZ() * div, event.getFrom().getYaw(),
                event.getFrom().getPitch());
            to = event.getPortalTravelAgent().findOrCreate(to);
            event.setTo(to);
          }
        }
        log.debug(
            "[PortalHandler] [PortalType.NETHER] used for user " + event.getEntityType().toString()
                .toLowerCase() + " to get to world " + toWorldString);
      }
    }
  }

  private int getPortalType(Location loc) {

    Block mainBlock = loc.getBlock();
    Material toCheck;
    for (BlockFace face : new BlockFace[]
        {
            BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST, BlockFace.NORTH_WEST
        }) {
      toCheck = mainBlock.getRelative(face).getType();
      if (toCheck == Material.END_PORTAL) {
        return END_PORTAL;
      } else if (toCheck == Material.NETHER_PORTAL) {
        return NETHER_PORTAL;
      }
    }

    return PortalHandler.UNKNOWN_PORTAL;
  }
}

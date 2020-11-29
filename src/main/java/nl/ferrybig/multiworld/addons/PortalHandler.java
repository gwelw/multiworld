package nl.ferrybig.multiworld.addons;

import nl.ferrybig.multiworld.handler.DataHandler;
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
  private final DataHandler dataHandler;
  private final Logger log = LoggerFactory.getLogger(PortalHandler.class);
  private final boolean handleEndPortals;
  private boolean enabled = false;

  public PortalHandler(DataHandler dataHandler, boolean handleEndPortals) {
    this.dataHandler = dataHandler;
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

  public void save() throws ConfigException {
    if (!enabled) {
      throw new IllegalStateException("Not enabled");
    }
    this.dataHandler.save();
    this.log.info("[PortalHandler] saved!");
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public void onDisable() {
    if (!enabled) {
      throw new IllegalStateException("Already disabled");
    }
    enabled = false;
  }

  @Override
  public void onEnable() {
    if (enabled) {
      throw new IllegalStateException("Already enabled");
    }
    this.enabled = true;
    this.load();
  }

  public void add(String world1, String world2) {
    if (this.handleEndPortals) {
      this.dataHandler.getWorldManager().setEndPortal(world1, world2);
    } else {
      this.dataHandler.getWorldManager().setPortal(world1, world2);
    }
  }

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
      if (mapType == END_PORTAL) {
        log.debug("[PortalHandler] got PortalType.END.");
        InternalWorld from = this.dataHandler.getWorldManager()
            .getInternalWorld(event.getFrom().getWorld().getName(), true);
        String toWorldString = from.getEndPortalWorld();
        if (!toWorldString.isEmpty()) {
          World toWorld = this.dataHandler.getWorldManager().getWorld(toWorldString);
          if (toWorld != null) {
            World.Environment toDim = toWorld.getEnvironment();
            World.Environment fromDim = event.getFrom().getWorld().getEnvironment();

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
      if (!this.handleEndPortals && mapType == NETHER_PORTAL) {
        log.debug("[PortalHandler] got PortalType.NETHER.");
        String toWorldString = this.dataHandler.getWorldManager()
            .getInternalWorld(event.getFrom().getWorld().getName(), true).getPortalWorld();
        if (!toWorldString.isEmpty()) {
          World toWorld = this.dataHandler.getWorldManager().getWorld(toWorldString);
          if (toWorld != null) {
            World.Environment toDim = toWorld.getEnvironment(), fromDim = event.getFrom().getWorld()
                .getEnvironment();
            float div;
            if (fromDim == toDim) {
              div = 1.0f;
            } else if (fromDim == World.Environment.NETHER) {
              div = 8.0f;
            } else if (toDim == World.Environment.NETHER) {
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

  private int getPortalType(Location location) {
    Block mainBlock = location.getBlock();
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

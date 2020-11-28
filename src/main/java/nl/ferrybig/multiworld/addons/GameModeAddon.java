package nl.ferrybig.multiworld.addons;

import static org.bukkit.GameMode.CREATIVE;
import static org.bukkit.GameMode.SURVIVAL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import nl.ferrybig.multiworld.MultiWorldPlugin;
import nl.ferrybig.multiworld.Utils;
import nl.ferrybig.multiworld.api.events.FlagChanceEvent;
import nl.ferrybig.multiworld.api.events.GameModeChangeByWorldEvent;
import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.flags.FlagValue;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModeAddon implements Listener, MultiworldAddon {

  private final Logger log = LoggerFactory.getLogger(GameModeAddon.class);

  private final DataHandler dataHandler;
  private HashMap<UUID, PlayerData> creativePlayers;
  private HashMap<UUID, RemovePlayerTask> tasks;
  private boolean isEnabled = false;

  public GameModeAddon(DataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    log.trace("Got PlayerJoinEvent");
    if (this.tasks.containsKey(event.getPlayer().getUniqueId())) {
      Bukkit.getScheduler().cancelTask(this.tasks.get(event.getPlayer().getUniqueId()).getTaskId());
      this.tasks.remove(event.getPlayer().getUniqueId());
    } else {
      this.checkAndAddPlayer(event.getPlayer(), event.getPlayer().getWorld());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event) {
    log.trace("Got PlayerQuitEvent");
    Player player = event.getPlayer();
    this.tasks.put(player.getUniqueId(), new RemovePlayerTask(player));
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerChanceWorld(PlayerChangedWorldEvent event) {
    log.trace("Got PlayerChanceWorldEvent");
    this.reloadPlayer(event.getPlayer(), event.getPlayer().getWorld());
  }

  @EventHandler
  public void onFlagChance(FlagChanceEvent event) {
    if (!isEnabled) {
      return;
    }
    if (event.getFlag() == FlagName.CREATIVE_WORLD) {
      for (Player p : event.getWorld().getBukkitWorld().getPlayers()) {
        this.reloadPlayer(p, p.getWorld());
      }
    }
  }

  private void removePlayer(Player player) {
    if (!isEnabled) {
      return;
    }
    PlayerData tmp = this.creativePlayers.get(player.getUniqueId());
    if (tmp != null) {
      removePlayerAction(player, tmp);
      this.creativePlayers.remove(player.getUniqueId());
    }
  }

  private void removePlayerAction(Player player, PlayerData database) {
    if (database != null) {
      database.putOnPlayer(player);
    }
    player.setGameMode(SURVIVAL);
    new GameModeChangeByWorldEvent(player, SURVIVAL).call();
    log.debug("Changing {} game mode back to SURVIVAL", player.getName());
  }

  private boolean isAffected(Player player) {
    return Utils.hasPermission(player, "creativemode");
  }

  private void checkAndAddPlayer(Player player, World toWorld) {
    if (!isEnabled || !this.isAffected(player)) {
      return;
    }
    if (this.dataHandler.getWorldManager().getFlag(toWorld.getName(), FlagName.CREATIVE_WORLD)
        == FlagValue.TRUE) {
      this.addPlayer(player);
    }
  }

  private void addPlayer(Player player) {
    if (!this.creativePlayers.containsKey(player.getUniqueId())) {
      this.creativePlayers.put(player.getUniqueId(),
          this.dataHandler.getNode(DataHandler.OPTIONS_GAMEMODE_INV) ? PlayerData
              .getFromPlayer(player)
              : null);
    }
    player.setGameMode(CREATIVE);
    new GameModeChangeByWorldEvent(player, CREATIVE).call();
    log.trace("Changing {} game mode to CREATIVE", player.getDisplayName());
  }

  public void reloadPlayer(Player player, World toWorld) {
    if (!isEnabled || !this.isAffected(player)) {
      return;
    }
    if (this.dataHandler.getWorldManager().getFlag(toWorld.getName(), FlagName.CREATIVE_WORLD)
        == FlagValue.TRUE) {
      if (!this.creativePlayers.containsKey(player.getUniqueId()) && player.isOnline()) {
        this.addPlayer(player);
      }
    } else {
      if (this.creativePlayers.containsKey(player.getUniqueId())) {
        this.removePlayer(player);
      }
    }
  }

  @Override
  public void onDisable() {
    Iterator<Map.Entry<UUID, PlayerData>> loop = this.creativePlayers.entrySet().iterator();
    Map.Entry<UUID, PlayerData> playerDataEntry;
    while (loop.hasNext()) {
      playerDataEntry = loop.next();
      Player player = Bukkit.getPlayer(playerDataEntry.getKey());
      if (player != null) {
        this.removePlayerAction(player, playerDataEntry.getValue());
      }
    }
    for (RemovePlayerTask task : this.tasks.values()) {
      task.run();
      Bukkit.getScheduler().cancelTask(task.getTaskId());
    }
    this.creativePlayers.clear();
    this.creativePlayers = null;
    this.isEnabled = false;
  }

  @Override
  public void onEnable() {
    this.tasks = new HashMap<>(Math.min(20, Bukkit.getMaxPlayers()));
    this.creativePlayers = new HashMap<>(Math.min(20, Bukkit.getMaxPlayers()));
    Bukkit.getScheduler().scheduleSyncDelayedTask(MultiWorldPlugin.getInstance(), () -> {
      for (Player player : Bukkit.getOnlinePlayers()) {
        checkAndAddPlayer(player, player.getWorld());
      }
    });
    this.isEnabled = true;
  }

  @Override
  public boolean isEnabled() {
    return this.isEnabled;
  }

  private class RemovePlayerTask implements Runnable {

    private final Player player;
    private final int taskId = Bukkit.getScheduler()
        .scheduleSyncDelayedTask(MultiWorldPlugin.getInstance(), this);

    RemovePlayerTask(Player player) {
      this.player = player;
    }

    @Override
    public void run() {
      try {
        GameModeAddon.this.removePlayer(player);
        player.saveData();
      } finally {
        GameModeAddon.this.tasks.remove(player.getUniqueId());
        GameModeAddon.this.creativePlayers.remove(player.getUniqueId());
      }
    }

    public int getTaskId() {
      return taskId;
    }
  }
}

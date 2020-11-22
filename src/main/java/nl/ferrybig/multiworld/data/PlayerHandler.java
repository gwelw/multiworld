package nl.ferrybig.multiworld.data;

import java.util.List;
import nl.ferrybig.multiworld.exception.NotAPlayerException;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerHandler {

  private final DataHandler h;
  private final MyLogger log;

  public PlayerHandler(DataHandler h) {
    this.h = h;
    this.log = h.getLogger();
  }

  public void moveAllPlayers(World from, World to, String warpOutMsg) {
    if (from == to) {
      throw new IllegalArgumentException();
    }
    List<Player> playerList = from.getPlayers();
    Player[] players = new Player[playerList.size()];
    for (Player tmp : playerList.toArray(players)) {
      this.movePlayer(tmp, to);
      if (!"".equals(warpOutMsg)) {
        tmp.sendMessage(warpOutMsg);
      }
    }
  }

  public void movePlayer(Player player, World world) {
    movePlayer(player, world.getSpawnLocation());
  }

  public void movePlayer(Player player, Location loc) {
    World world = loc.getWorld();
    Chunk chunk;
    world.loadChunk(chunk = world.getChunkAt(loc));
    player.teleport(loc);
    this.log.fine("Warping " + player.getDisplayName() + " to location " + loc.toString()); //NOI18N
  }

  public Player getPlayer(String name) {
    return Bukkit.getPlayer(name);
  }

  public Player getPlayer(CommandSender s) throws NotAPlayerException {
    if (s instanceof Player) {
      return (Player) s;
    }
    throw new NotAPlayerException();
  }
}

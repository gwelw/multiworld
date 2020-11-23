package nl.ferrybig.multiworld.data;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import nl.ferrybig.multiworld.exception.NotAPlayerException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerHandler {

  private static final Logger log = LoggerFactory.getLogger(PlayerHandler.class);

  public void moveAllPlayers(World from, World to, String warpOutMsg) {
    checkArgument(from == to, "From and to location must be differenet!");

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

  public void movePlayer(Player player, Location location) {
    player.teleportAsync(location);
    log.trace("Warping {} to location {}", player.getName(), location);
  }

  public Player getPlayer(String name) {
    return Bukkit.getPlayer(name);
  }

  public Player getPlayer(CommandSender sender) throws NotAPlayerException {
    if (sender instanceof Player) {
      return (Player) sender;
    }
    throw new NotAPlayerException();
  }
}

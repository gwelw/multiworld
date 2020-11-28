package nl.ferrybig.multiworld.addons;

import java.util.Iterator;
import java.util.List;
import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.data.InternalWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class WorldChatSeparatorPlugin implements Listener, MultiworldAddon {

  private final DataHandler dataHandler;
  private boolean isEnabled = false;

  public WorldChatSeparatorPlugin(DataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onChat(AsyncPlayerChatEvent event) {
    if (!isEnabled) {
      return;
    }

    InternalWorld internalWorld = dataHandler.getWorldManager()
        .getInternalWorld(event.getPlayer().getWorld().getName(), true);
    boolean maySendChat = dataHandler.getWorldManager().getFlag(internalWorld.getName(), FlagName.SEND_CHAT)
        .getAsBoolean();
    if (!maySendChat) {
      List<Player> worldPlayers = event.getPlayer().getWorld().getPlayers();
      Iterator<Player> recipients = event.getRecipients().iterator();
      Player player;
      while (recipients.hasNext()) {
        player = recipients.next();
        if (!worldPlayers.contains(player)) {
          recipients.remove();
        }
      }
      return;
    }

    for (InternalWorld world : dataHandler.getWorldManager().getLoadedWorlds()) {
      if (world != internalWorld && !dataHandler.getWorldManager().getFlag(world.getName(), FlagName.RECEIVE_CHAT)
          .getAsBoolean()) {
        event.getRecipients().removeAll(world.getWorld().getPlayers());
      }
    }
  }

  @Override
  public void onDisable() {
    this.isEnabled = false;
  }

  @Override
  public void onEnable() {
    this.isEnabled = true;
  }

  @Override
  public boolean isEnabled() {
    return this.isEnabled;
  }
}

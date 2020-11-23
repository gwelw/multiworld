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
  public void onChat(AsyncPlayerChatEvent evt) {
    if (!isEnabled) {
      return;
    }
    InternalWorld w = dataHandler.getWorldManager()
        .getInternalWorld(evt.getPlayer().getWorld().getName(), true);
    boolean maySendChat = dataHandler.getWorldManager().getFlag(w.getName(), FlagName.SENDCHAT)
        .getAsBoolean();
    if (!maySendChat) {
      List<Player> worldPlayers = evt.getPlayer().getWorld().getPlayers();
      Iterator<Player> recipients = evt.getRecipients().iterator();
      Player p;
      while (recipients.hasNext()) {
        p = recipients.next();
        if (!worldPlayers.contains(p)) {
          recipients.remove();
        }
      }
      return;
    }
    for (InternalWorld world : dataHandler.getWorldManager().getLoadedWorlds()) {
      if (world != w && !dataHandler.getWorldManager().getFlag(world.getName(), FlagName.RECIEVECHAT)
          .getAsBoolean()) {
        evt.getRecipients().removeAll(world.getWorld().getPlayers());
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

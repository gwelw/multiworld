package nl.ferrybig.multiworld.api.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameModeChanceByWorldEvent extends MultiWorldEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private final GameMode newMode;

  public GameModeChanceByWorldEvent(Player player, GameMode newMode) {
    super();
    this.player = player;
    this.newMode = newMode;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @NotNull
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public Player getPlayer() {
    return player;
  }

  public GameMode getNewMode() {
    return newMode;
  }
}

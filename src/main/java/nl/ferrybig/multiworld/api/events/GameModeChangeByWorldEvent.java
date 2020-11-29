package nl.ferrybig.multiworld.api.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameModeChangeByWorldEvent extends MultiWorldEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;
  private final GameMode newGameMode;

  public GameModeChangeByWorldEvent(Player player, GameMode newGameMode) {
    super();
    this.player = player;
    this.newGameMode = newGameMode;
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

  public GameMode getNewGameMode() {
    return newGameMode;
  }
}

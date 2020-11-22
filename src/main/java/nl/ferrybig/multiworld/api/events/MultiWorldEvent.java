package nl.ferrybig.multiworld.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class MultiWorldEvent extends Event {

  public MultiWorldEvent() {
    super();
  }

  public void call() {
    Bukkit.getPluginManager().callEvent(this);
  }
}

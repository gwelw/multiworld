package nl.ferrybig.multiworld.addons;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerData extends Object implements Cloneable {

  private final ItemStack[] inventory;
  private final ItemStack[] armor;
  private final int xp;
  private final int level;
  private final int onFire;

  private PlayerData(ItemStack[] arg1, ItemStack[] arg2, int xp, int level, int onFire) {
    this.inventory = arg1;
    this.armor = arg2;
    this.xp = xp;
    this.level = level;
    this.onFire = onFire;
  }

  public static PlayerData getFromPlayer(Player player) {
    PlayerInventory inv = player.getInventory();
    ItemStack[] inventory = new ItemStack[inv.getContents().length];

    for (int i = 0; i < inventory.length; i++) {
      ItemStack item = inv.getContents()[i];
      if (item != null) {
        inventory[i] = item.clone();
      }
    }
    return new PlayerData(inventory, inv.getArmorContents().clone(), player.getTotalExperience(),
        player.getLevel(), player.getFireTicks());
  }

  public void putOnPlayer(Player player) {
    PlayerInventory inv = player.getInventory();
    inv.clear();
    inv.setContents(this.inventory.clone());
    inv.setArmorContents(this.armor.clone());
    player.setLevel(level);
    player.setTotalExperience(xp);
    player.setFireTicks(onFire);

  }

  @Override
  public PlayerData clone() throws CloneNotSupportedException {
    return (PlayerData) super.clone();
  }
}
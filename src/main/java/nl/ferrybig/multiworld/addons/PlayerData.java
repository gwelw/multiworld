package nl.ferrybig.multiworld.addons;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerData implements Cloneable {

  private final ItemStack[] inventoryContent;
  private final ItemStack[] armor;
  private final int xp;
  private final int level;
  private final int onFire;

  private PlayerData(ItemStack[] inventoryContent, ItemStack[] armor, int xp, int level, int onFire) {
    this.inventoryContent = inventoryContent;
    this.armor = armor;
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
    PlayerInventory inventory = player.getInventory();
    inventory.clear();
    inventory.setContents(this.inventoryContent.clone());
    inventory.setArmorContents(this.armor.clone());
    player.setLevel(level);
    player.setTotalExperience(xp);
    player.setFireTicks(onFire);
  }

  @Override
  public PlayerData clone() throws CloneNotSupportedException {
    return (PlayerData) super.clone();
  }
}
package nl.ferrybig.multiworld.command.plugin;

import nl.ferrybig.multiworld.addons.AddonHandler;
import nl.ferrybig.multiworld.addons.AddonHolder;
import nl.ferrybig.multiworld.addons.PortalHandler;
import nl.ferrybig.multiworld.command.ArgumentType;
import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.data.DataHandler;
import nl.ferrybig.multiworld.data.WorldHandler;
import nl.ferrybig.multiworld.translation.Translation;
import nl.ferrybig.multiworld.translation.message.MessageCache;
import nl.ferrybig.multiworld.translation.message.PackedMessageData;
import org.bukkit.command.CommandSender;

public class LinkCommand extends Command {

  public static final PackedMessageData PATTERN_PORTAL_NETHER = MessageCache
      .custom("%portal%", "nether portal");
  public static final PackedMessageData PATTERN_PORTAL_END = MessageCache
      .custom("%portal%", "end portal");
  private final AddonHandler addonHandler;
  private final DataHandler dataHandler;
  private final boolean isForEndPortals;
  private final WorldHandler worlds;
  private final PackedMessageData usedPattern;

  public LinkCommand(DataHandler dataHandler, WorldHandler worldHandler, AddonHandler addonHandler,
      boolean isForEndPortals) {
    super(isForEndPortals ? "link.end" : "link.nether",
        " Links " + (isForEndPortals ? "end" : "nether") + " portals between worlds");
    this.dataHandler = dataHandler;
    this.addonHandler = addonHandler;
    this.isForEndPortals = isForEndPortals;
    this.worlds = worldHandler;
    this.usedPattern = isForEndPortals ? PATTERN_PORTAL_END : PATTERN_PORTAL_NETHER;
  }

  @Override
  public void runCommand(CommandStack stack) {
    PortalHandler portal = getPortalHandler();
    if (portal == null) {
      stack.sendMessage(MessageType.ERROR, Translation.COMMAND_LINK_PORTALHANDLER_NOT_FOUND);
    }
    String[] split = stack.getArguments();
    if (split.length == 1) {
      if (worlds.getWorld(split[0], false) == null) {
        stack.sendMessage(
            MessageType.ERROR,
            Translation.WORLD_NOT_FOUND,
            MessageCache.WORLD.get(split[0]));
        return;
      }
      portal.add(split[0], null);
      stack.sendMessageBroadcast(
          MessageType.SUCCESS,
          Translation.COMMAND_LINK_REMOVE_LINK,
          MessageCache.WORLD.get(split[0]),
          this.usedPattern
      );
    } else if (split.length == 2) {
      if (worlds.getWorld(split[0], false) == null) {
        stack.sendMessage(
            MessageType.ERROR,
            Translation.WORLD_NOT_FOUND,
            MessageCache.WORLD.get(split[0]));
        return;
      }
      if (worlds.getWorld(split[1], false) == null) {
        stack.sendMessage(
            MessageType.ERROR,
            Translation.WORLD_NOT_FOUND,
            MessageCache.WORLD.get(split[1]));
        return;
      }
      portal.add(split[0], split[1]);
      stack.sendMessageBroadcast(MessageType.SUCCESS,
          Translation.COMMAND_LINK_SET_LINK,
          MessageCache.WORLD.get(split[0]),
          MessageCache.TARGET.get(split[1]),
          this.usedPattern);

      this.dataHandler.scheduleSave();
    } else {
      stack.sendMessageUsage(stack.getCommandLabel(),
          (isForEndPortals ? ArgumentType.valueOf("link-end") : ArgumentType.valueOf("link")),
          ArgumentType.TARGET_WORLD,
          ArgumentType.NEW_WORLD_NAME);
    }
  }

  private PortalHandler getPortalHandler() {
    AddonHolder<?> holder;
    if (isForEndPortals) {
      holder = this.addonHandler.getPlugin("EndPortalHandler");
    } else {
      holder = this.addonHandler.getPlugin("NetherPortalHandler");
    }
    if (holder == null) {
      return null;
    }
    return (PortalHandler) holder.getAddon();
  }

  @Override
  public String[] calculateMissingArguments(CommandSender sender, String commandName,
      String[] split) {
    if (split.length == 0) {
      return this.calculateMissingArgumentsWorld("");
    } else if (split.length == 1) {
      return this.calculateMissingArgumentsWorld(split[0]);
    } else if (split.length == 2) {
      return this.calculateMissingArgumentsWorld(split[1]);
    } else {
      return EMPTY_STRING_ARRAY;
    }
  }
}

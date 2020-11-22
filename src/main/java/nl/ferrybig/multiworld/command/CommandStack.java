package nl.ferrybig.multiworld.command;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public interface CommandStack extends MessageLogger {

  String getCommandLabel();

  CommandSender getSender();

  String[] getArguments();

  CommandStack getParent();

  boolean hasPermission(String permission);

  Location getLocation();

  Builder newStack();

  Builder editStack();

  interface Builder {

    Builder setSender(CommandSender sender);

    Builder setLocation(Location loc);

    Builder popArguments(int number);

    Builder setArguments(String[] args);

    Builder setCommandLabel(String commandLabel);

    Builder setMessageLogger(MessageLogger logger);

    CommandStack build();
  }
}

package nl.ferrybig.multiworld.command.flag;

import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;

public class FlagListCommand extends Command {

  public FlagListCommand() {
    super("flaglist", "gets al flags on the server");
  }

  @Override
  public void runCommand(CommandStack stack) {
    stack.sendMessage(MessageType.SUCCES, FlagName.makeFlagList());
  }

}

package nl.ferrybig.multiworld.command.config;

import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.data.ReloadHandler;
import nl.ferrybig.multiworld.translation.Translation;

public class LoadCommand extends Command {

  private final ReloadHandler reloadHandler;

  public LoadCommand(ReloadHandler reloadHandler) {
    super("load", "Reloads the nl.ferrybig.multiworld configuration file");
    this.reloadHandler = reloadHandler;
  }

  @Override
  public void runCommand(CommandStack stack) {
    if (this.reloadCommand()) {
      stack.sendMessage(MessageType.SUCCESS, Translation.COMMAND_RELOAD_SUCCES);
    } else {
      stack.sendMessage(MessageType.ERROR, Translation.COMMAND_RELOAD_FAIL);
    }
  }

  private boolean reloadCommand() {
    return reloadHandler.reload();
  }
}

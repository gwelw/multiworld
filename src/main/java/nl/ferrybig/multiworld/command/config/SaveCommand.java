package nl.ferrybig.multiworld.command.config;

import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;
import nl.ferrybig.multiworld.data.ReloadHandler;
import nl.ferrybig.multiworld.translation.Translation;

public class SaveCommand extends Command {

  private final ReloadHandler reloadHandler;

  public SaveCommand(ReloadHandler reload) {
    super("save", "Saves multiworld data to the disk");
    this.reloadHandler = reload;
  }

  @Override
  public void runCommand(CommandStack stack) {
    if (this.saveCommand()) {
      stack.sendMessageBroadcast(MessageType.SUCCESS, Translation.COMMAND_SAVE_SUCCES);
    } else {
      stack.sendMessageBroadcast(MessageType.ERROR, Translation.COMMAND_SAVE_FAIL);
    }
  }

  private boolean saveCommand() {
    return this.reloadHandler.save();
  }
}

package nl.ferrybig.multiworld.command.world;

import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;

public class DifficultyCommand extends Command {

  private final boolean setter;

  public DifficultyCommand(boolean setter) {
    super("difficulty." + (setter ? "set" : "get"),
        (setter ? "Set" : "Get") + "s the difficulty of a world");
    this.setter = setter;
  }

  @Override
  public void runCommand(CommandStack stack) {
    throw new UnsupportedOperationException(
        "Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}

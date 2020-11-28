package nl.ferrybig.multiworld.command.other;

import nl.ferrybig.multiworld.command.Command;
import nl.ferrybig.multiworld.command.CommandStack;
import nl.ferrybig.multiworld.command.MessageType;

public class EasterEggCommand extends Command {

  public EasterEggCommand() {
    super(null, "Just a small hidden easter egg, ;P");
  }

  @Override
  public void runCommand(CommandStack stack) {
    if (stack.getCommandLabel().equals("multiworld")) {
      stack.sendMessage(MessageType.HIDDEN_SUCCESS,
          " ___###\n"
              + "   /oo\\ |||\n"
              + "   \\  / \\|/\n"
              + "   /\"\"\\  I\n"
              + "()|    |(I)\n"
              + "   \\  /  I\n"
              + "  /\"\"\"\"\\ I\n"
              + " |      |I\n"
              + " |      |I\n"
              + "  \\____/ I");
    }
  }
}

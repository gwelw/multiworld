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
    if (stack.getCommandLabel().equals("nl/ferrybig/multiworld")) {
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
    } else {
      switch (stack.getDebugLevel()) {
        case NONE:
          stack.sendMessage(MessageType.HIDDEN_SUCCESS,
              "There are no easter eggs inside this program!");
          break;
        case V:
          stack.sendMessage(MessageType.HIDDEN_SUCCESS,
              "There realy are no easter eggs inside this program!");
          break;
        case VV:
          stack.sendMessage(MessageType.HIDDEN_SUCCESS,
              "Did you know I really didn't added a easter egg into this command?");
          break;
        case VVV:
          stack.sendMessage(MessageType.HIDDEN_SUCCESS, "STOP IT, or I will crash");
          break;
        case VVVV:
          stack.sendMessage(MessageType.HIDDEN_SUCCESS,
              "If I give you a easter egg, will you stop then?");
          break;
        case VVVVV:
          stack.sendMessage(MessageType.HIDDEN_SUCCESS, "Here you have your easter egg....");
          stack.sendMessage(MessageType.HIDDEN_SUCCESS, "        /---\\                   ");
          stack.sendMessage(MessageType.HIDDEN_SUCCESS, "-----/      \\------------------");
          stack.sendMessage(MessageType.HIDDEN_SUCCESS, "-----\\      /------------------");
          stack.sendMessage(MessageType.HIDDEN_SUCCESS, "        \\---/                   ");
          break;
        case VVVVVV:
          stack.sendMessage(MessageType.HIDDEN_SUCCESS,
              "What is it? Its a snake that consumed a minecraft block");
          break;
      }
    }

  }
}

package nl.ferrybig.multiworld.command;

import nl.ferrybig.multiworld.translation.message.PackedMessageData;

public interface MessageLogger {

  void sendMessage(MessageType type, String message);

  void sendMessage(MessageType type, PackedMessageData... message);

  void sendMessageUsage(String commandLabel, ArgumentType... types);

  void sendMessageBroadcast(MessageType type, String message);

  void sendMessageBroadcast(MessageType type, PackedMessageData... message);
}

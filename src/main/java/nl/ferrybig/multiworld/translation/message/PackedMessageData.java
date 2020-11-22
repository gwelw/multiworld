package nl.ferrybig.multiworld.translation.message;

public interface PackedMessageData {

  PackedMessageData NO_CONSOLE_MESSAGE = new PackedMessageData() {

    @Override
    public String getBinary() {
      return "No-Console";
    }

    @Override
    public String transformMessage(String prevFormat) {
      return prevFormat;
    }
  };

  String transformMessage(String prevFormat);

  String getBinary();

}

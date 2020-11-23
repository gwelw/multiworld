package nl.ferrybig.multiworld.translation.message;

import java.util.LinkedHashMap;
import java.util.Map;

public enum MessageCache {
  WORLD("%world%"),
  PLAYER("%player%"),
  SEED("%seed%"),
  ENVIOMENT("%envioment%"),
  GENERATOR("%generator%"),
  GENERATOR_OPTION("%option%"),
  NUMBER("%percent%"),
  DIFFICULTY("%difficulty%"),
  TARGET("%target%"),
  FLAG("%flag%"),
  FLAG_VALUE("%value%"),
  ;
  private final CachingMap cache;
  private final String replacePattern;

  MessageCache(String replacePattern) {
    this.replacePattern = replacePattern;
    this.cache = new CachingMap(10);
  }

  public static PackedMessageData custom(final String pattern, final String data) {
    return new PackedMessageData() {
      final String binary = pattern + "\n" + data;

      @Override
      public String getBinary() {
        return binary;
      }

      @Override
      public String transformMessage(String prevFormat) {
        return prevFormat.replace(pattern, data);
      }
    };
  }

  public PackedMessageData get(final String data) {
    return this.cache.computeIfAbsent(data, d -> new PackedMessageData() {
      final String binary = MessageCache.this.replacePattern + "\n" + d;

      @Override
      public String getBinary() {
        return binary;
      }

      @Override
      public String transformMessage(String prevFormat) {
        return prevFormat.replace(MessageCache.this.replacePattern, d);
      }
    });
  }

  private static class CachingMap extends LinkedHashMap<String, PackedMessageData> {

    private static final long serialVersionUID = 14436556L;
    private final int cacheSize;

    public CachingMap(int cacheSize) {
      this.cacheSize = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, PackedMessageData> entry) {
      return this.size() > cacheSize;
    }

  }
}

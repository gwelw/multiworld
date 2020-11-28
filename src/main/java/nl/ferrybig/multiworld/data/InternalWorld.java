package nl.ferrybig.multiworld.data;

import static java.util.Objects.hash;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import nl.ferrybig.multiworld.api.flag.FlagName;
import nl.ferrybig.multiworld.flags.FlagValue;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

public class InternalWorld {

  private String worldName;
  private long worldSeed;
  private World.Environment worldType = World.Environment.NORMAL;
  private ChunkGenerator worldGen;
  private String options;
  private Map<FlagName, FlagValue> flags;
  private String fullGeneratorName;
  private String portalLink = "";
  private String endLink = "";
  private Difficulty difficulty = Difficulty.NORMAL;
  private WorldType type = WorldType.NORMAL;

  public WorldType getType() {
    return type;
  }

  public World getWorld() {
    return Bukkit.getWorld(worldName);
  }

  public String getName() {
    return this.worldName;
  }

  public World.Environment getEnv() {
    return this.worldType;
  }

  public long getSeed() {
    return this.worldSeed;
  }

  public String getOptions() {
    return this.options;
  }

  public String getPortalWorld() {
    return this.portalLink;
  }

  public Map<FlagName, FlagValue> getFlags() {
    return flags;
  }

  public ChunkGenerator getGen() {
    return this.worldGen;
  }

  public String getFullGeneratorName() {
    return this.fullGeneratorName;
  }

  public String getEndPortalWorld() {
    return this.endLink;
  }

  public Difficulty getDifficulty() {
    return difficulty;
  }

  public String getWorldType() {
    if (this.worldGen == null) {
      if (this.worldType == Environment.NORMAL) {
        return "Normal world";
      } else if (this.worldType == Environment.NETHER) {
        return "Nether world";
      } else if (this.worldType == Environment.THE_END) {
        return "End world";
      }
    } else {
      if (this.getFullGeneratorName().equals("NULLGEN")) {
        if (this.worldType == Environment.NORMAL) {
          return "Normal world with unknown external generator";
        } else if (this.worldType == Environment.NETHER) {
          return "Nether world with unknown external generator";
        } else if (this.worldType == Environment.THE_END) {
          return "End world with unknown external generator";
        }
      } else if (this.getFullGeneratorName().startsWith("PLUGIN")) {
        if (this.worldType == Environment.NORMAL) {
          return "Normal world with external generator: " + this.getOptions();
        } else if (this.worldType == Environment.NETHER) {
          return "Nether world with external generator: " + this.getOptions();
        } else if (this.worldType == Environment.THE_END) {
          return "End world with external generator: " + this.getOptions();
        }
      } else {
        if (this.worldType == Environment.NORMAL) {
          return "Normal world with internal generator: " + this.getFullGeneratorName() + (
              this.getOptions().isEmpty() ? "" : ": " + this.getOptions());
        } else if (this.worldType == Environment.NETHER) {
          return "Nether world with internal generator: " + this.getFullGeneratorName() + (
              this.getOptions().isEmpty() ? "" : ": " + this.getOptions());
        } else if (this.worldType == Environment.THE_END) {
          return "End world with internal generator: " + this.getFullGeneratorName() + (
              this.getOptions().isEmpty() ? "" : ": " + this.getOptions());
        }
      }
    }

    return "Unknown world";
  }

  public InternalWorld setWorldName(String worldName) {
    this.worldName = worldName;
    return this;
  }

  public InternalWorld setWorldSeed(long worldSeed) {
    this.worldSeed = worldSeed;
    return this;
  }

  public InternalWorld setWorldType(Environment worldType) {
    this.worldType = worldType;
    return this;
  }

  public InternalWorld setWorldGen(ChunkGenerator worldGen) {
    this.worldGen = worldGen;
    return this;
  }

  public InternalWorld setOptions(String options) {
    this.options = options;
    return this;
  }

  public InternalWorld setFlags(
      Map<FlagName, FlagValue> flags) {
    this.flags = flags;
    return this;
  }

  public InternalWorld setFullGeneratorName(String fullGeneratorName) {
    this.fullGeneratorName = fullGeneratorName;
    return this;
  }

  public InternalWorld setPortalLink(String portalLink) {
    this.portalLink = portalLink;
    return this;
  }

  public InternalWorld setEndLink(String endLink) {
    this.endLink = endLink;
    return this;
  }

  public InternalWorld setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
    return this;
  }

  public InternalWorld setType(WorldType type) {
    this.type = type;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof InternalWorld)) {
      return false;
    }
    InternalWorld that = (InternalWorld) o;
    return worldSeed == that.worldSeed &&
        Objects.equals(worldName, that.worldName) &&
        worldType == that.worldType &&
        Objects.equals(worldGen, that.worldGen) &&
        Objects.equals(fullGeneratorName, that.fullGeneratorName) &&
        Objects.equals(options, that.options) &&
        Objects.equals(flags, that.flags) &&
        Objects.equals(portalLink, that.portalLink) &&
        Objects.equals(endLink, that.endLink) &&
        difficulty == that.difficulty;
  }

  @Override
  public int hashCode() {
    return hash(worldName, worldSeed, worldType, worldGen, options, flags, portalLink, endLink,
        difficulty, fullGeneratorName);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", InternalWorld.class.getSimpleName() + "[", "]")
        .add("worldName='" + worldName + "'")
        .add("worldSeed=" + worldSeed)
        .add("worldType=" + worldType)
        .add("worldGen=" + worldGen)
        .add("options='" + options + "'")
        .add("flags=" + flags)
        .add("fullGeneratorName='" + fullGeneratorName + "'")
        .add("portalLink='" + portalLink + "'")
        .add("endLink='" + endLink + "'")
        .add("difficulty=" + difficulty)
        .add("type=" + type)
        .toString();
  }
}

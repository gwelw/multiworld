package nl.ferrybig.multiworld.worldgen.util;

import java.io.Serializable;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public final class ChunkMaker implements Cloneable, Serializable {

  private static final long serialVersionUID = 111234729L;

  private final Material[][] chunk;
  private final int ySize;

  public ChunkMaker(World world) {
    this(world.getMaxHeight());
  }

  public ChunkMaker(int maxHeight) {
    this(new Material[maxHeight / 16][], maxHeight);
  }

  public ChunkMaker(Material[][] chunk, int maxHeight) {
    this.chunk = chunk;
    this.ySize = maxHeight;
  }

  private void checkAccess(int x, int y, int z) {
    if ((x < 0) || (x >= 16)) {
      throw new IllegalArgumentException("X must be 0 <= x < 16");
    }
    if ((y < 0) || (y >= this.ySize)) {
      throw new IllegalArgumentException("Y must be 0 <= x < 128");
    }
    if ((z < 0) || (z >= 16)) {
      throw new IllegalArgumentException("Z must be 0 <= x < 16");
    }
  }

  private void checkSelection(int x1, int y1, int z1, int x2, int y2, int z2) {
    if ((x1 > x2) || (y1 > y2) || (z1 > z2)) {
      throw new IllegalArgumentException("the first point must be smaller than the second");
    }
  }

  private void checkSelection(Pointer loc1, Pointer loc2) {
    this.checkSelection(loc1.x, loc1.y, loc1.z, loc2.x, loc2.y, loc2.z);
  }

  private void checkPointers(Pointer... list) {
    for (Pointer p : list) {
      this.checkPointer(p);
    }
  }

  private void checkPointer(Pointer p) {
    if (p.getMainChunk() != this) {
      throw new IllegalArgumentException("The given pointer does not point to this chunk");
    }
  }

  private void setB(int x, int y, int z, Material blkid) {
    if (chunk[y >> 4] == null) {
      chunk[y >> 4] = new Material[4096];
      Arrays.fill(chunk[y >> 4], Material.AIR);
    }
    chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;

  }

  public void setBlock(int x, int y, int z, Material block) {
    this.checkAccess(x, y, z);
    this.setB(x, y, z, block);
  }

  public void setBlock(Pointer loc, Material block) {
    this.setB(loc.x, loc.y, loc.z, block);
  }


  private Material getB(int x, int y, int z) {
    if (chunk[y >> 4] == null) {
      return Material.AIR;
    }

    return chunk[y >> 4][((y & 0xF) << 8) | (z << 4) | x];
  }

  public Material getBlock(int x, int y, int z) {
    this.checkAccess(x, y, z);
    return this.getB(x, y, z);
  }

  public Material getBlock(Pointer loc) {
    this.checkPointer(loc);
    return this.getB(loc.x, loc.y, loc.z);
  }


  private void action(Pointer loc1, Pointer loc2, ChunkHelper c) {
    this.checkPointers(loc1, loc2);
    this.checkSelection(loc1, loc2);
    for (int x = loc1.getX(); x <= loc2.getX(); x++) {
      for (int z = loc1.getZ(); z <= loc2.getZ(); z++) {
        for (int y = loc1.getY(); y <= loc2.getY(); y++) {
          c.run(new Pointer(x, y, z), loc2, loc1);
        }
      }
    }
  }
  public void cuboid(int x1, int y1, int z1, int x2, int y2, int z2, Material block) {
    this.cuboid(this.getPointer(x1, y1, z1), this.getPointer(x2, y2, z2), block);
  }

  public void cuboid(Pointer loc1, Pointer loc2, final Material block) {
    this.action(loc1, loc2, (target, selection1, selection2) -> target.setBlock(block));
  }

  public void replace(int x1, int y1, int z1, int x2, int y2, int z2, Material blockFrom,
      Material blockTo) {
    this.replace(this.getPointer(x1, y1, z1), this.getPointer(x2, y2, z2), blockFrom, blockTo);
  }

  public void replace(Pointer loc1, Pointer loc2, final Material from, final Material to) {
    this.action(loc1, loc2, (target, selection1, selection2) -> {
      if (target.getBlock() == from) {
        target.setBlock(to);
      }
    });
  }

  public void walls(Pointer loc1, Pointer loc2, final Material block) {
    this.action(loc1, loc2, (t, a, b) -> {
      if ((t.x == a.x) || (t.x == b.x) || (t.z == a.z) || (t.z == b.z)) {
        t.setBlock(block);
      }

    });
  }

  public void walls(int x1, int y1, int z1, int x2, int y2, int z2, Material block) {
    this.walls(this.getPointer(x1, y1, z1), this.getPointer(x2, y2, z2), block);
  }

  public Pointer getPointer(int x, int y, int z) {
    return new Pointer(x, y, z);
  }

  public Material[][] getRawChunk() {
    return this.chunk;
  }

  @Override
  public ChunkMaker clone() throws CloneNotSupportedException {
    return (ChunkMaker) super.clone();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ChunkMaker other = (ChunkMaker) obj;

    return Arrays.equals(this.chunk, other.chunk);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.chunk) ^ 1213675258;
  }

  @Override
  public String toString() {
    return "ChunkMaker:" + this.hashCode();
  }

  public ChunkGenerator.ChunkData toChunkData(ChunkGenerator.ChunkData chunkData) {
    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        for (int y = 0; y < 256; y++) {
          if (chunk[y >> 4] != null) {
            chunkData.setBlock(x, y, z, getB(x, y, z));
          }
        }
      }
    }
    return chunkData;
  }

  private interface ChunkHelper {

    void run(Pointer target, Pointer selection1, Pointer selection2);
  }

  public class Pointer implements Cloneable, Serializable, Comparable<Pointer> {

    private static final long serialVersionUID = 56874873276L;

    public final int x;

    public final int y;

    public final int z;

    public Pointer() {
      this(0, 0, 0);
    }

    public Pointer(int x, int y, int z) {
      ChunkMaker.this.checkAccess(x, y, z);
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public ChunkMaker getMainChunk() {
      return ChunkMaker.this;
    }

    public int getX() {
      return this.x;
    }

    public int getY() {
      return this.y;
    }

    public int getZ() {
      return this.z;
    }

    public Material getBlock() {
      return ChunkMaker.this.getB(this.x, this.y, this.z);
    }

    public void setBlock(Material block) {
      ChunkMaker.this.setB(this.x, this.y, this.z, block);
    }

    private int getIndex() {
      return (this.x * 16 + this.z) * 128 + this.y;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Pointer other = (Pointer) obj;
      if (this.x != other.x) {
        return false;
      }
      if (this.y != other.y) {
        return false;
      }

      return this.z == other.z;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 4 * hash + this.x;
      hash = 7 * hash + this.y;
      hash = 4 * hash + this.z;
      return hash;
    }

    @Override
    public String toString() {
      return "Pointer{x=" + x + ",y=" + y + ",z=" + z + "}";
    }

    @Override
    public Pointer clone() throws CloneNotSupportedException {
      return (Pointer) super.clone();
    }

    @Override
    public int compareTo(Pointer o) {
      return this.getIndex() - o.getIndex();
    }
  }
}

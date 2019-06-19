package main.com.pyratron.pugmatt.bedrockconnect.utils.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

/**
 * author: Angelic47 Nukkit Project
 */
public class NukkitRandom {

    protected long seed;

    // constructor
    public NukkitRandom() {
        this(-1);
    }

    public NukkitRandom(long seeds) {
        if (seeds == -1) {
            seeds = System.currentTimeMillis() / 1000L;
        }
        this.setSeed(seeds);
    }

    public void setSeed(long seeds) {
        CRC32 crc32 = new CRC32();
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt((int) seeds);
        crc32.update(buffer.array());
        this.seed = crc32.getValue();
    }

    public int nextSignedInt() {
        int t = (((int) ((this.seed * 65535) + 31337) >> 8) + 1337);
        this.seed ^= t;
        return t;
    }

    public int nextInt() {
        return this.nextSignedInt() & 0x7fffffff;
    }

    public double nextDouble() {
        return (double) this.nextInt() / 0x7fffffff;
    }

    public float nextFloat() {
        return (float) this.nextInt() / 0x7fffffff;
    }

    public float nextSignedFloat() {
        return (float) this.nextInt() / 0x7fffffff;
    }

    public double nextSignedDouble() {
        return (double) this.nextSignedInt() / 0x7fffffff;
    }

    public boolean nextBoolean() {
        return (this.nextSignedInt() & 0x01) == 0;
    }

    public int nextRange() {
        return nextRange(0, 0x7fffffff);
    }

    public int nextRange(int start) {
        return nextRange(start, 0x7fffffff);
    }

    public int nextRange(int start, int end) {
        return start + (this.nextInt() % (end + 1 - start));
    }

    public int nextBoundedInt(int bound) {
        return this.nextInt() % bound;
    }
}
package main.com.pyratron.pugmatt.bedrockconnect;

// References to this: https://github.com/NukkitX/Nukkit/blob/4ce13f2afb9a1a330df63a482bee9995ce1f96d5/src/main/java/cn/nukkit/level/format/anvil/ChunkRequestTask.java
// https://github.com/Nukkit/Nukkit/blob/master/src/main/java/cn/nukkit/level/format/generic/BaseFullChunk.java
// https://github.com/Nukkit/Nukkit/blob/master/src/main/java/cn/nukkit/level/format/leveldb/Chunk.java
// https://github.com/NukkitX/Nukkit/blob/4ce13f2afb9a1a330df63a482bee9995ce1f96d5/src/main/java/cn/nukkit/level/format/mcregion/McRegion.java

import java.nio.ByteBuffer;

public class Chunk{

    byte[] ids;
    byte[] meta;
    byte[] heightMap;
    byte[] skyLight;
    byte[] blockLight;
    byte[] biomes;
    byte[] blockEntities;

    public Chunk(int x, int z) {
        ids = getBlockIdArray();
        meta = getBlockDataArray();
        heightMap = getHeightMapArray(x, z);
        skyLight = getBlockSkyLightArray();
        blockLight = getBlockLightArray();
        biomes = getBiomeIdArray();
        blockEntities = getBlockEntitiesArray();
    }

    public byte[] encode() {
        return ByteBuffer.allocate(
                16 * 16 * 128
                        + 16 * 16 * 64
                        + heightMap.length
                        + biomes.length
                        + this.blockEntities.length)
                .put(ids)
                .put(meta)
                .put(heightMap)
                .put(biomes)
                .put(blockEntities)
                .array();
    }

    public byte[] getBlockIdArray() {
        byte[] blocks = new byte[16*128*16];
        for (int i = 0; i < 16*128*16; i++) {
            int d = 1;
            blocks[i] = (byte) d;
        }
        return blocks;
    }

    public byte[] getBlockDataArray() {
        byte[] blocks = new byte[16384];
        for (int i = 0; i < 16384; i++) {
            int d = 0;
            blocks[i] = (byte) d;
        }
        return blocks;
    }

    public byte[] getBlockSkyLightArray() {
        byte[] lights = new byte[16384];
        for (int i = 0; i < 16384; i++) {
            int d = 0;
            lights[i] = (byte) d;
        }
        return lights;
    }

    public byte[] getBlockLightArray() {
        byte[] lights = new byte[16384];
        for (int i = 0; i < 16384; i++) {
            int d = 0;
            lights[i] = (byte) d;
        }
        return lights;
    }

    public byte[] getHeightMapArray(int x, int z) {
        byte[] heights = new byte[256];
        for (int i = 0; i < 256; i++) {
            int d = 1;
            heights[i] = (byte) ((z << 4) | x & 0xFF);
        }
        return heights;
    }

    public byte[] getBiomeIdArray() {
        byte[] ids = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            int d = i;
            ids[i] = (byte) (d >> 24);
        }
        return ids;
    }

    public byte[] getBlockEntitiesArray() {
        byte[] ids = new byte[32];
        for (int i = 0; i < 32; i++) {
            int d = i;
            ids[i] = (byte) d;
        }
        return ids;
    }
}

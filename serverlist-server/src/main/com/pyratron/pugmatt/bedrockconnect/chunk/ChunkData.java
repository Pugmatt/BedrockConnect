package main.com.pyratron.pugmatt.bedrockconnect.chunk;

import main.com.pyratron.pugmatt.bedrockconnect.utils.BinaryStream;

import java.util.Arrays;

// reference: https://github.com/DragonetMC/DragonProxy/blob/master/protocol/src/main/java/org/dragonet/protocol/type/chunk/ChunkData.java
// Original Author: HoverEpic

public class ChunkData extends BinaryStream {

    /**
     * 16x16x16 section of the chunk. The array's keys also indicate the
     * section's height (the 3rd element of the array will be the 3rd section
     * from bottom, starting at `y=24`). The amount of sections should be in a
     * range from 0 (empty chunk) to 16.
     */
    public Section[] sections = new Section[0];
    public short[] heights = new short[256];
    /**
     * Biomes in order `xz`.
     */
    public byte[] biomes = new byte[256];
    /**
     * Colums where there are world borders (in format `xz`). This feature
     * hasn't been implemented in the game yet and crashes the client.
     */
    public byte[] borders = new byte[0];
    public ExtraData[] extraData = new ExtraData[0];
    /**
     * Additional data for the chunk's block entities (tiles), encoded in the
     * same way as BlockEntityData.nbt is. The position is given by the `Int`
     * tags `x`, `y`, `z` which are added to the block's compound tag together
     * with the `String` tag `id` that contains the name of the tile in pascal
     * case. Wrong encoding or missing tags may result in the block becoming
     * invisible.
     */
    public byte[] blockEntities = new byte[0];

    public ChunkData(boolean empty) {
        if(empty) {
            sections = new Section[16];
            for (int cy = 0; cy < 16; cy++) {
                sections[cy] = new Section();
                if (cy < 6)
                    Arrays.fill(sections[cy].blockIds, (byte) 1);
            }
        }
    }

    public ChunkData(Section[] sections, short[] heights, byte[] biomes, byte[] borders, ExtraData[] extraData,
                     byte[] blockEntities) {
        this.sections = sections;
        this.heights = heights;
        this.biomes = biomes;
        this.borders = borders;
        this.extraData = extraData;
        this.blockEntities = blockEntities;
    }

    public void encode() {
        reset();
        putByte((byte) (sections.length & 0xff));
        for (Section s : sections) {
            s.encode(this);
        }
        for (short h : heights) {
            this.putLShort(h);
        }
        this.put(biomes);
        this.putByte((byte) (borders.length & 0xFF));
        this.put(borders);
        this.putVarInt(extraData.length);
        for (ExtraData extra : extraData) {
            extra.encode(this);
        }
        this.put(blockEntities);
    }

}

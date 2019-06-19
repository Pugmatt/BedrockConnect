package main.com.pyratron.pugmatt.bedrockconnect.chunk;

// reference: https://github.com/DragonetMC/DragonProxy/blob/master/protocol/src/main/java/org/dragonet/protocol/type/chunk/Section.java
// Original Author: HoverEpic

import main.com.pyratron.pugmatt.bedrockconnect.utils.BinaryStream;

public class Section {

    public byte storageVers = 0;
    public byte[] blockIds = new byte[4096];
    public byte[] blockMetas = new byte[2048];

    public Section(byte storageVers, byte[] blockIds, byte[] blockMetas) {
        this.storageVers = storageVers;
        this.blockIds = blockIds;
        this.blockMetas = blockMetas;
    }

    public Section() {

    }

    public void encode(BinaryStream out) {
        out.putByte(storageVers);
        out.put(blockIds);
        out.put(blockMetas);
    }

    public void decode(BinaryStream in) {
        storageVers = (byte) (in.getByte() & 0xFF);
        blockIds = in.get(4096);
        blockMetas = in.get(2048);
    }
}

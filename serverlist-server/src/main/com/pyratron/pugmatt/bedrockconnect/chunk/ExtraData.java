package main.com.pyratron.pugmatt.bedrockconnect.chunk;

// reference: https://github.com/DragonetMC/DragonProxy/blob/master/protocol/src/main/java/org/dragonet/protocol/type/chunk/ExtraData.java
// Original Author: HoverEpic

import main.com.pyratron.pugmatt.bedrockconnect.utils.BinaryStream;

public class ExtraData {

    private int key;
    private short value;

    public int getKey() {
        return key;
    }

    public short getValue() {
        return value;
    }

    public ExtraData() {

    }

    public ExtraData(int key, short value) {
        this.key = key;
        this.value = value;
    }

    public void encode(BinaryStream out) {
        out.putVarInt(key);
        out.putLShort(value);
    }

    public void decode(BinaryStream in) {
        key = (int) in.getUnsignedVarInt();
        value = (short) in.getLShort();
    }

}

package main.com.pyratron.pugmatt.bedrockconnect.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * author: MagicDroidX Nukkit Project
 */
public class BinaryStream {

    public int offset;
    private byte[] buffer = new byte[32];
    private int count;

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public BinaryStream() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }

    public BinaryStream(byte[] buffer) {
        this(buffer, 0);
    }

    public BinaryStream(byte[] buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
        this.count = buffer.length;
    }

    public void reset() {
        this.buffer = new byte[32];
        this.offset = 0;
        this.count = 0;
    }

    public byte[] getBuffer() {
        return Arrays.copyOf(buffer, count);
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
        this.count = buffer == null ? -1 : buffer.length;
    }

    public void setBuffer(byte[] buffer, int offset) {
        this.setBuffer(buffer);
        this.setOffset(offset);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public byte[] get() {
        return this.get(this.count - this.offset);
    }

    public byte[] get(int len) {
        if (len < 0) {
            this.offset = this.count - 1;
            return new byte[0];
        }
        len = Math.min(len, this.getCount() - this.offset);
        this.offset += len;
        return Arrays.copyOfRange(this.buffer, this.offset - len, this.offset);
    }

    public void put(byte[] bytes) {
        if (bytes == null) {
            return;
        }

        this.ensureCapacity(this.count + bytes.length);

        System.arraycopy(bytes, 0, this.buffer, this.count, bytes.length);
        this.count += bytes.length;
    }

    public long getLong() {
        return Binary.readLong(this.get(8));
    }

    public void putLong(long l) {
        this.put(Binary.writeLong(l));
    }

    public int getInt() {
        return Binary.readInt(this.get(4));
    }

    public void putInt(int i) {
        this.put(Binary.writeInt(i));
    }

    public long getLLong() {
        return Binary.readLLong(this.get(8));
    }

    public void putLLong(long l) {
        this.put(Binary.writeLLong(l));
    }

    public int getLInt() {
        return Binary.readLInt(this.get(4));
    }

    public void putLInt(int i) {
        this.put(Binary.writeLInt(i));
    }

    public int getShort() {
        return Binary.readShort(this.get(2));
    }

    public void putShort(int s) {
        this.put(Binary.writeShort(s));
    }

    public int getLShort() {
        return Binary.readLShort(this.get(2));
    }

    public void putLShort(int s) {
        this.put(Binary.writeLShort(s));
    }

    public float getFloat() {
        return getFloat(-1);
    }

    public float getFloat(int accuracy) {
        return Binary.readFloat(this.get(4), accuracy);
    }

    public void putFloat(float v) {
        this.put(Binary.writeFloat(v));
    }

    public float getLFloat() {
        return getLFloat(-1);
    }

    public float getLFloat(int accuracy) {
        return Binary.readLFloat(this.get(4), accuracy);
    }

    public void putLFloat(float v) {
        this.put(Binary.writeLFloat(v));
    }

    public int getTriad() {
        return Binary.readTriad(this.get(3));
    }

    public void putTriad(int triad) {
        this.put(Binary.writeTriad(triad));
    }

    public int getLTriad() {
        return Binary.readLTriad(this.get(3));
    }

    public void putLTriad(int triad) {
        this.put(Binary.writeLTriad(triad));
    }

    public boolean getBoolean() {
        return this.getByte() == 0x01;
    }

    public void putBoolean(boolean bool) {
        this.putByte((byte) (bool ? 1 : 0));
    }

    public int getByte() {
        return this.buffer[this.offset++] & 0xff;
    }

    public void putByte(byte b) {
        this.put(new byte[]{b});
    }

     /** public void putEntityLink(PEEntityLink link) {
        putVarLong(link.riding);
        putVarLong(link.rider);
        putByte(link.type);
        putByte(link.unknownByte);
    }

//    public PEEntityLink getEntityLink() {
//        return new PEEntityLink(
//                getVarLong(),
//                getVarLong(),
//                getByte(),
//                getByte());
//    }

    public Map<String, GameRule> getGameRules() {
        int count = (int) getUnsignedVarInt();
        Map<String, GameRule> rules = new HashMap<>();
        for (int i = 0; i < count; i++) {
            GameRule rule = GameRule.read(this);
            rules.put(rule.name, rule);
        }
        return rules;
    }

    public void putGameRules(Map<String, GameRule> rules) {
        if (rules == null) {
            putUnsignedVarInt(0);
            return;
        }
        putUnsignedVarInt(rules.size());
        for (GameRule rule : rules.values()) {
            rule.write(this);
        }
    }

    public float getByteRotation() {
        return (((float) (getByte() & 0xFF)) * (360 / 256));
    }

    public void putByteRotation(float rotation) {
        putByte((byte) (((int) (rotation / (360 / 256))) & 0xFF));
    }

    /**
     * Reads a list of Attributes from the stream.
     *
     * @return Attribute[]
     */
     /**
    public PEEntityAttribute[] getAttributeList() throws Exception {
        List<PEEntityAttribute> list = new ArrayList<>();
        long count = this.getUnsignedVarInt();

        for (int i = 0; i < count; ++i) {
            String name = this.getString();
            PEEntityAttribute attr = PEEntityAttribute.findAttribute(name);
            if (attr != null) {
                attr.min = getLFloat();
                attr.currentValue = getLFloat();
                attr.max = this.getLFloat();
                list.add(attr);
            } else {
                throw new Exception("Unknown attribute type \"" + name + "\"");
            }
        }

        return list.stream().toArray(PEEntityAttribute[]::new);
    }
    /**

    /**
     * Writes a list of Attributes to the packet buffer using the standard
     * format.
     */
    /**
    public void putAttributeList(PEEntityAttribute[] attributes) {
        this.putUnsignedVarInt(attributes.length);
        for (PEEntityAttribute attribute : attributes) {
            this.putString(attribute.name);
            this.putLFloat(attribute.min);
            this.putLFloat(attribute.currentValue);
            this.putLFloat(attribute.max);
        }
    }

    public void putUUID(UUID uuid) {
        this.put(Binary.writeUUID(uuid));
    }

    public UUID getUUID() {
        return Binary.readUUID(this.get(16));
    }

    public void putSkin(Skin skin) {
        this.putString(skin.getModel());
        this.putByteArray(skin.getData());
        this.putByteArray(skin.getCape().getData());
    }

    public Skin getSkin() {
        String modelId = this.getString();
        byte[] skinData = this.getByteArray();
        return new Skin(skinData, modelId);
    }

    public Slot getSlot() {
        int id = this.getVarInt();

        if (id <= 0) {
            return Slot.AIR;
        }
        int auxValue = this.getVarInt();
        int data = auxValue >> 8;
        if (data == Short.MAX_VALUE) {
            data = -1;
        }
        int cnt = auxValue & 0xff;

        int nbtLen = this.getLShort();
        byte[] nbt;
        CompoundTag tag = null;
        if (nbtLen > 0) {
            nbt = this.get(nbtLen);
            try {
                tag = NBTIO.read(nbt, ByteOrder.LITTLE_ENDIAN, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // TODO
        int canPlaceOn = this.getVarInt();
        if (canPlaceOn > 0) {
            for (int i = 0; i < canPlaceOn; ++i) {
                this.getString();
            }
        }

        // TODO
        int canDestroy = this.getVarInt();
        if (canDestroy > 0) {
            for (int i = 0; i < canDestroy; ++i) {
                this.getString();
            }
        }

        return new Slot(id, data, cnt, tag);
    }

    public void putSlot(Slot item) {
        if (item == null || item.id == 0) {
            this.putVarInt(0);
            return;
        }

        this.putVarInt(item.id);
        int auxValue = ((item.damage & 0x7fff) << 8) | item.count;
        this.putVarInt(auxValue);
        byte[] nbt;
        if (item.tag != null) {
            try {
                nbt = NBTIO.write(item.tag, ByteOrder.LITTLE_ENDIAN, false);
            } catch (IOException e) {
                e.printStackTrace();
                nbt = new byte[0];
            }
        } else {
            nbt = new byte[0];
        }
        this.putLShort(nbt.length);
        this.put(nbt);
        this.putVarInt(0); // TODO CanPlaceOn entry count
        this.putVarInt(0); // TODO CanDestroy entry count
    }

    **/


    public byte[] getByteArray() {
        return this.get((int) this.getUnsignedVarInt());
    }

    public void putByteArray(byte[] b) {
        this.putUnsignedVarInt(b.length);
        this.put(b);
    }

    public String getString() {
        return new String(this.getByteArray(), StandardCharsets.UTF_8);
    }

    public void putString(String string) {
        byte[] b = string.getBytes(StandardCharsets.UTF_8);
        this.putByteArray(b);
    }

    public long getUnsignedVarInt() {
        return VarInt.readUnsignedVarInt(this);
    }

    public void putUnsignedVarInt(long v) {
        VarInt.writeUnsignedVarInt(this, v);
    }

    public int getVarInt() {
        return VarInt.readVarInt(this);
    }

    public void putVarInt(int v) {
        VarInt.writeVarInt(this, v);
    }

    public long getVarLong() {
        return VarInt.readVarLong(this);
    }

    public void putVarLong(long v) {
        VarInt.writeVarLong(this, v);
    }

    public long getUnsignedVarLong() {
        return VarInt.readUnsignedVarLong(this);
    }

    public void putUnsignedVarLong(long v) {
        VarInt.writeUnsignedVarLong(this, v);
    }

    /**
    public BlockPosition getBlockPosition() {
        return new BlockPosition(this.getVarInt(), (int) this.getUnsignedVarInt(), this.getVarInt());
    }

    public BlockPosition getSignedBlockPosition() {
        return new BlockPosition(getVarInt(), getVarInt(), getVarInt());
    }

    public void putSignedBlockPosition(BlockPosition v) {
        putVarInt(v.x);
        putVarInt(v.y);
        putVarInt(v.z);
    }

    public void putBlockPosition(BlockPosition v) {
        this.putBlockPosition(v.x, v.y, v.z);
    }

    public void putBlockPosition(int x, int y, int z) {
        this.putVarInt(x);
        this.putUnsignedVarInt(y);
        this.putVarInt(z);
    }

    public Vector3F getVector3F() {
        return new Vector3F(this.getLFloat(4), this.getLFloat(4), this.getLFloat(4));
    }

    public void putVector3F(Vector3F v) {
        if (v == null) {
            this.putVector3F(0f, 0f, 0f);
        } else {
            this.putVector3F(v.x, v.y, v.z);
        }
    }

    public void putVector3F(float x, float y, float z) {
        this.putLFloat(Math.round(x * 10000f) / 10000f);
        this.putLFloat(Math.round(y * 10000f) / 10000f);
        this.putLFloat(Math.round(z * 10000f) / 10000f);
    }

    /**
     * Reads and returns an EntityUniqueID
     *
     * @return int
     */
    public long getEntityUniqueId() {
        return this.getVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void putEntityUniqueId(long eid) {
        this.putVarLong(eid);
    }

    /**
     * Reads and returns an EntityRuntimeID
     */
    public long getEntityRuntimeId() {
        return this.getUnsignedVarLong();
    }

    /**
     * Writes an EntityUniqueID
     */
    public void putEntityRuntimeId(long eid) {
        this.putUnsignedVarLong(eid);
    }

    public boolean feof() {
        return this.offset < 0 || this.offset >= this.buffer.length;
    }

    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buffer.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buffer.length;
        int newCapacity = oldCapacity << 1;

        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }

        if (newCapacity - MAX_ARRAY_SIZE > 0) {
            newCapacity = hugeCapacity(minCapacity);
        }
        this.buffer = Arrays.copyOf(buffer, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }
}

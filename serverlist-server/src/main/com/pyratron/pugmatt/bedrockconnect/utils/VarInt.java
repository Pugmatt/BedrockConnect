package main.com.pyratron.pugmatt.bedrockconnect.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Tool class for VarInt or VarLong operations.
 * <p>
 * Some code from http://wiki.vg/Protocol.
 *
 * @author MagicDroidX
 * @author lmlstarqaq
 */
public final class VarInt {

    private VarInt() {
        // no instance
    }

    /**
     * @param v Signed int
     * @return Unsigned encoded int
     */
    public static long encodeZigZag32(int v) {
        // Note: the right-shift must be arithmetic
        return (long) ((v << 1) ^ (v >> 31));
    }

    /**
     * @param v Unsigned encoded int
     * @return Signed decoded int
     */
    public static int decodeZigZag32(long v) {
        return (int) (v >> 1) ^ -(int) (v & 1);
    }

    /**
     * @param v Signed long
     * @return Unsigned encoded long
     */
    public static long encodeZigZag64(long v) {
        return (v << 1) ^ (v >> 63);
    }

    /**
     * @param v Signed encoded long
     * @return Unsigned decoded long
     */
    public static long decodeZigZag64(long v) {
        return (v >>> 1) ^ -(v & 1);
    }

    /**
     * @param stream BinaryStream
     * @return Signed int
     */
    public static int readVarInt(BinaryStream stream) {
        return decodeZigZag32(readUnsignedVarInt(stream));
    }

    /**
     * @param stream InputStream
     * @return Signed int
     */
    public static int readVarInt(InputStream stream) throws IOException {
        return decodeZigZag32(readUnsignedVarInt(stream));
    }

    /**
     * @param stream BinaryStream
     * @return Unsigned int
     */
    public static long readUnsignedVarInt(BinaryStream stream) {
        return read(stream, 5);
    }

    /**
     * @param stream InputStream
     * @return Unsigned int
     */
    public static long readUnsignedVarInt(InputStream stream) throws IOException {
        return read(stream, 5);
    }

    /**
     * @param stream BinaryStream
     * @return Signed long
     */
    public static long readVarLong(BinaryStream stream) {
        return decodeZigZag64(readUnsignedVarLong(stream));
    }

    /**
     * @param stream InputStream
     * @return Signed long
     */
    public static long readVarLong(InputStream stream) throws IOException {
        return decodeZigZag64(readUnsignedVarLong(stream));
    }

    /**
     * @param stream BinaryStream
     * @return Unsigned long
     */
    public static long readUnsignedVarLong(BinaryStream stream) {
        return read(stream, 10);
    }

    /**
     * @param stream InputStream
     * @return Unsigned long
     */
    public static long readUnsignedVarLong(InputStream stream) throws IOException {
        return read(stream, 10);
    }

    /**
     * @param stream BinaryStream
     * @param value  Signed int
     */
    public static void writeVarInt(BinaryStream stream, int value) {
        writeUnsignedVarInt(stream, encodeZigZag32(value));
    }

    /**
     * @param stream OutputStream
     * @param value  Signed int
     */
    public static void writeVarInt(OutputStream stream, int value) throws IOException {
        writeUnsignedVarInt(stream, encodeZigZag32(value));
    }

    /**
     * @param stream BinaryStream
     * @param value  Unsigned int
     */
    public static void writeUnsignedVarInt(BinaryStream stream, long value) {
        write(stream, value);
    }

    /**
     * @param stream OutputStream
     * @param value  Unsigned int
     */
    public static void writeUnsignedVarInt(OutputStream stream, long value) throws IOException {
        write(stream, value);
    }

    /**
     * @param stream BinaryStream
     * @param value  Signed long
     */
    public static void writeVarLong(BinaryStream stream, long value) {
        writeUnsignedVarLong(stream, encodeZigZag64(value));
    }

    /**
     * @param stream OutputStream
     * @param value  Signed long
     */
    public static void writeVarLong(OutputStream stream, long value) throws IOException {
        writeUnsignedVarLong(stream, encodeZigZag64(value));
    }

    /**
     * @param stream BinaryStream
     * @param value  Unsigned long
     */
    public static void writeUnsignedVarLong(BinaryStream stream, long value) {
        write(stream, value);
    }

    /**
     * @param stream OutputStream
     * @param value  Unsigned long
     */
    public static void writeUnsignedVarLong(OutputStream stream, long value) throws IOException {
        write(stream, value);
    }

    private static long read(BinaryStream stream, int maxSize) {
        long value = 0;
        int size = 0;
        int b;
        while (((b = stream.getByte()) & 0x80) == 0x80) {
            value |= (long) (b & 0x7F) << (size++ * 7);
            if (size >= maxSize) {
                throw new IllegalArgumentException("VarLong too big");
            }
        }

        return value | ((long) (b & 0x7F) << (size * 7));
    }

    private static long read(InputStream stream, int maxSize) throws IOException {
        long value = 0;
        int size = 0;
        int b;
        while (((b = stream.read()) & 0x80) == 0x80) {
            value |= (long) (b & 0x7F) << (size++ * 7);
            if (size >= maxSize) {
                throw new IllegalArgumentException("VarLong too big");
            }
        }

        return value | ((long) (b & 0x7F) << (size * 7));
    }

    private static void write(BinaryStream stream, long value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number
            // rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            stream.putByte(temp);
        } while (value != 0);
    }

    private static void write(OutputStream stream, long value) throws IOException {
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number
            // rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            stream.write(temp);
        } while (value != 0);
    }
}
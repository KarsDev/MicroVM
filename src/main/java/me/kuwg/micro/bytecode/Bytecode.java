package me.kuwg.micro.bytecode;

import java.nio.ByteBuffer;

import static me.kuwg.micro.constants.Constants.TypeConstants.*;

public class Bytecode {
    private final byte[] bytecode;
    private int readerIndex;

    public Bytecode(int size) {
        this.bytecode = new byte[size];
        this.readerIndex = 0;
    }

    public void load(final byte[] bytes) {
        if (bytes.length > bytecode.length) {
            throw new IllegalArgumentException("Exceeded memory (%d > %d)".formatted(bytes.length, bytecode.length));
        }
        System.arraycopy(bytes, 0, bytecode, 0, bytes.length);
    }

    public Object read() {
        if (readerIndex >= bytecode.length) {
            throw new IndexOutOfBoundsException("End of bytecode reached.");
        }

        byte typeIdentifier = readByte();
        return switch (typeIdentifier) {
            case BYTE_TYPE -> readByte();
            case INT_TYPE -> readInt();
            case LONG_TYPE -> readLong();
            case DOUBLE_TYPE -> readDouble();
            case STRING_TYPE -> readString();
            default -> throw new IllegalArgumentException("Unknown type identifier: " + typeIdentifier);
        };
    }

    public byte readByte() {
        if (readerIndex >= bytecode.length) {
            throw new IndexOutOfBoundsException("End of bytecode reached while reading byte.");
        }
        return bytecode[readerIndex++];
    }

    public int readInt() {
        if (readerIndex + Integer.BYTES > bytecode.length) {
            throw new IndexOutOfBoundsException("Not enough bytes to read an int.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytecode, readerIndex, Integer.BYTES);
        readerIndex += Integer.BYTES;
        return buffer.getInt();
    }

    public long readLong() {
        if (readerIndex + Long.BYTES > bytecode.length) {
            throw new IndexOutOfBoundsException("Not enough bytes to read a long.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytecode, readerIndex, Long.BYTES);
        readerIndex += Long.BYTES;
        return buffer.getLong();
    }

    public double readDouble() {
        if (readerIndex + Double.BYTES > bytecode.length) {
            throw new IndexOutOfBoundsException("Not enough bytes to read a double.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytecode, readerIndex, Double.BYTES);
        readerIndex += Double.BYTES;
        return buffer.getDouble();
    }

    public String readString() {
        if (readerIndex >= bytecode.length) {
            throw new IndexOutOfBoundsException("End of bytecode reached while reading string.");
        }
        int length = readByte(); // Read string length
        if (readerIndex + length > bytecode.length) {
            throw new IndexOutOfBoundsException("Not enough bytes to read the string.");
        }
        String str = new String(bytecode, readerIndex, length); // Read string content
        readerIndex += length;
        return str;
    }

    public int readerIndex() {
        return readerIndex;
    }

    public void readerIndex(int readerIndex) {
        this.readerIndex = readerIndex;
    }

    public int length() {
        return bytecode.length;
    }

    public byte peekNextByte() {
        return bytecode[readerIndex + 1];
    }
}
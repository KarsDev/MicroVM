package me.kuwg.micro.bytecode;

import java.nio.ByteBuffer;

import static me.kuwg.micro.constants.Constants.BooleanConstants.TRUE;
import static me.kuwg.micro.constants.Constants.TypeConstants.*;

/**
 * The {@code Bytecode} class provides methods to read and manipulate a sequence of bytes that represent various
 * data types. This class acts as a bytecode reader that supports reading different primitive data types and
 * strings from a byte array.
 *
 * <p>The bytecode is stored in a private byte array, and the class maintains an internal index to track the
 * position of the next read operation. The class allows loading byte data into the bytecode and provides
 * functionality to read data types including bytes, integers, longs, doubles, strings, and booleans.</p>
 *
 * <p>This implementation ensures that the read operations handle bounds checking, throwing appropriate
 * exceptions when attempting to read beyond the limits of the bytecode array.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     Bytecode bytecode = new Bytecode(1024);
 *     bytecode.load(new byte[]{0, 1, 2, 3, 4, 5}); // Load data
 *     int value = (int) bytecode.read(); // Read as int
 * </pre>
 */
public class Bytecode {
    private final byte[] bytecode;
    private int readerIndex;

    /**
     * Constructs a new {@code Bytecode} instance with the specified size.
     *
     * @param size the maximum size of the bytecode array.
     * @throws IllegalArgumentException if the specified size is less than or equal to zero.
     */
    public Bytecode(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero.");
        }
        this.bytecode = new byte[size];
        this.readerIndex = 0;
    }

    /**
     * Loads the specified byte array into the internal bytecode array.
     *
     * @param bytes the byte array to load.
     * @throws IllegalArgumentException if the length of the byte array exceeds the size of the bytecode array.
     */
    public void load(final byte[] bytes) {
        if (bytes.length > bytecode.length) {
            throw new IllegalArgumentException("Exceeded memory (%d > %d)".formatted(bytes.length, bytecode.length));
        }
        System.arraycopy(bytes, 0, bytecode, 0, bytes.length);
    }

    /**
     * Reads the next object from the bytecode array, determined by its type identifier.
     *
     * @return the read object, which can be a byte, int, long, double, string, or boolean.
     * @throws IndexOutOfBoundsException if the end of the bytecode is reached before reading.
     * @throws IllegalArgumentException if the type identifier is unknown.
     */
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
            case BOOLEAN_TYPE -> readBoolean();
            default -> throw new IllegalArgumentException("Unknown type identifier: " + typeIdentifier);
        };
    }

    /**
     * Reads a single byte from the bytecode array.
     *
     * @return the next byte from the bytecode array.
     * @throws IndexOutOfBoundsException if the end of the bytecode is reached while reading a byte.
     */
    public byte readByte() {
        if (readerIndex >= bytecode.length) {
            throw new IndexOutOfBoundsException("End of bytecode reached while reading byte.");
        }
        return bytecode[readerIndex++];
    }

    /**
     * Reads a 4-byte integer from the bytecode array.
     *
     * @return the next integer from the bytecode array.
     * @throws IndexOutOfBoundsException if there are not enough bytes to read an integer.
     */
    public int readInt() {
        if (readerIndex + Integer.BYTES > bytecode.length) {
            throw new IndexOutOfBoundsException("Not enough bytes to read an int.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytecode, readerIndex, Integer.BYTES);
        readerIndex += Integer.BYTES;
        return buffer.getInt();
    }

    /**
     * Reads an 8-byte long from the bytecode array.
     *
     * @return the next long from the bytecode array.
     * @throws IndexOutOfBoundsException if there are not enough bytes to read a long.
     */
    public long readLong() {
        if (readerIndex + Long.BYTES > bytecode.length) {
            throw new IndexOutOfBoundsException("Not enough bytes to read a long.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytecode, readerIndex, Long.BYTES);
        readerIndex += Long.BYTES;
        return buffer.getLong();
    }

    /**
     * Reads an 8-byte double from the bytecode array.
     *
     * @return the next double from the bytecode array.
     * @throws IndexOutOfBoundsException if there are not enough bytes to read a double.
     */
    public double readDouble() {
        if (readerIndex + Double.BYTES > bytecode.length) {
            throw new IndexOutOfBoundsException("Not enough bytes to read a double.");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytecode, readerIndex, Double.BYTES);
        readerIndex += Double.BYTES;
        return buffer.getDouble();
    }

    /**
     * Reads a string from the bytecode array. The string length is determined by the next byte in the bytecode.
     *
     * @return the next string from the bytecode array.
     * @throws IndexOutOfBoundsException if the end of the bytecode is reached while reading the string.
     */
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

    /**
     * Reads a boolean value from the bytecode array. A byte with the value of {@code TRUE} represents {@code true},
     * and any other value represents {@code false}.
     *
     * @return the next boolean from the bytecode array.
     * @throws IndexOutOfBoundsException if the end of the bytecode is reached while reading a boolean.
     */
    private boolean readBoolean() {
        if (readerIndex >= bytecode.length) {
            throw new IndexOutOfBoundsException("End of bytecode reached while reading byte.");
        }
        return bytecode[readerIndex++] == TRUE;
    }

    /**
     * Returns the current reader index, which indicates the position of the next read operation.
     *
     * @return the current reader index.
     */
    public int readerIndex() {
        return readerIndex;
    }

    /**
     * Sets the reader index to the specified position. This allows for random access in the bytecode array.
     *
     * @param readerIndex the new reader index position.
     * @throws IndexOutOfBoundsException if the specified index is out of bounds of the bytecode array.
     */
    public void readerIndex(int readerIndex) {
        if (readerIndex < 0 || readerIndex > bytecode.length) {
            throw new IndexOutOfBoundsException("Reader index out of bounds.");
        }
        this.readerIndex = readerIndex;
    }

    /**
     * Returns the length of the bytecode array.
     *
     * @return the length of the bytecode array.
     */
    public int length() {
        return bytecode.length;
    }

    /**
     * Peeks at the next byte without advancing the reader index.
     *
     * @return the byte at the next position in the bytecode array.
     * @throws IndexOutOfBoundsException if attempting to peek beyond the end of the bytecode array.
     */
    public byte peekNextByte() {
        if (readerIndex + 1 >= bytecode.length) {
            throw new IndexOutOfBoundsException("Peek index out of bounds.");
        }
        return bytecode[readerIndex + 1];
    }
}
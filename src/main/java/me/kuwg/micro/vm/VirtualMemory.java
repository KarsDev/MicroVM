package me.kuwg.micro.vm;

import static me.kuwg.micro.constants.Constants.BooleanConstants.FALSE;
import static me.kuwg.micro.constants.Constants.BooleanConstants.TRUE;
import static me.kuwg.micro.constants.Constants.TypeConstants.*;

public class VirtualMemory {
    private final byte[] memory;

    public VirtualMemory(int size) {
        memory = new byte[size];
    }

    public void store(int address, Object data) {
        if (address < 0 || address >= memory.length) {
            throw new IndexOutOfBoundsException("Address out of bounds: " + address);
        }

        switch (data) {
            case Byte b -> {
                storeByte(b, address);
            }
            case Integer i -> {
                storeInt(i, address);
            }
            case Long l -> {
                storeLong(l, address);
            }
            case Double d -> {
                storeDouble(d, address);
            }
            case String s -> {
                storeString(s, address);
            }
            case Boolean b -> {
                storeBoolean(b, address);
            }
            default -> throw new IllegalArgumentException("Could not store " + data.getClass().getSimpleName() + ", unknown type.");
        }
    }

    private void storeByte(byte b, int address) {
        memory[address] = BYTE_TYPE; // Store type identifier
        memory[address + 1] = b; // Store byte directly
    }

    private void storeInt(int i, int address) {
        if (address + 4 >= memory.length) {
            throw new IndexOutOfBoundsException("Not enough space to store an integer at address: " + address);
        }
        memory[address] = INT_TYPE; // Store type identifier
        // Store integer as 4 bytes
        memory[address + 1] = (byte) (i & 0xFF);
        memory[address + 2] = (byte) ((i >> 8) & 0xFF);
        memory[address + 3] = (byte) ((i >> 16) & 0xFF);
        memory[address + 4] = (byte) ((i >> 24) & 0xFF);
    }

    private void storeLong(long l, int address) {
        if (address + 8 >= memory.length) {
            throw new IndexOutOfBoundsException("Not enough space to store a long at address: " + address);
        }
        memory[address] = LONG_TYPE; // Store type identifier
        // Store long as 8 bytes
        for (int i = 0; i < 8; i++) {
            memory[address + 1 + i] = (byte) ((l >> (i * 8)) & 0xFF);
        }
    }

    private void storeDouble(double d, int address) {
        if (address + 8 >= memory.length) {
            throw new IndexOutOfBoundsException("Not enough space to store a double at address: " + address);
        }
        memory[address] = DOUBLE_TYPE; // Store type identifier
        long longBits = Double.doubleToRawLongBits(d);
        storeLong(longBits, address + 1); // Store double as long
    }

    private void storeString(String s, int address) {
        byte[] stringBytes = s.getBytes();
        if (address + 1 + stringBytes.length >= memory.length) {
            throw new IndexOutOfBoundsException("Not enough space to store a string at address: " + address);
        }
        memory[address] = STRING_TYPE; // Store type identifier
        // Store the length of the string as the first byte after type identifier
        memory[address + 1] = (byte) stringBytes.length;
        // Store the string bytes
        System.arraycopy(stringBytes, 0, memory, address + 2, stringBytes.length);
    }

    private void storeBoolean(boolean b, int address) {
        memory[address] = BOOLEAN_TYPE; // Store type identifier
        memory[address + 1] = b ? TRUE : FALSE; // Store byte directly
    }

    public Object load(int address) {
        if (address < 0 || address >= memory.length) {
            throw new IndexOutOfBoundsException("Address out of bounds: " + address);
        }

        byte typeIdentifier = memory[address];
        return switch (typeIdentifier) {
            case BYTE_TYPE -> loadByte(address);
            case INT_TYPE -> loadInt(address);
            case LONG_TYPE -> loadLong(address);
            case DOUBLE_TYPE -> loadDouble(address);
            case STRING_TYPE -> loadString(address);
            case BOOLEAN_TYPE -> loadBoolean(address);
            default -> throw new IllegalArgumentException("Unknown type identifier: " + typeIdentifier);
        };
    }

    private byte loadByte(int address) {
        return memory[address + 1]; // Return the byte directly after type identifier
    }

    private int loadInt(int address) {
        return (memory[address + 1] & 0xFF) | ((memory[address + 2] & 0xFF) << 8) |
                ((memory[address + 3] & 0xFF) << 16) | ((memory[address + 4] & 0xFF) << 24);
    }

    private long loadLong(int address) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result |= ((long) (memory[address + 1 + i] & 0xFF)) << (i * 8);
        }
        return result;
    }

    private double loadDouble(int address) {
        long longBits = loadLong(address);
        return Double.longBitsToDouble(longBits);
    }

    private String loadString(int address) {
        int length = memory[address + 1]; // The first byte after type identifier indicates the length
        byte[] stringBytes = new byte[length];
        System.arraycopy(memory, address + 2, stringBytes, 0, length); // Copy the string bytes
        return new String(stringBytes);
    }

    private boolean loadBoolean(int address) {
        return memory[address + 1] == TRUE;
    }
}
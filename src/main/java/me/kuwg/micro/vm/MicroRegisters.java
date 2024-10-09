package me.kuwg.micro.vm;

import java.util.ArrayList;
import java.util.List;

import static me.kuwg.micro.constants.Constants.TypeConstants.*;

public class MicroRegisters {
    private final List<Object> registers;

    public MicroRegisters(int size) {
        registers = new ArrayList<>(size);
        // Initialize with nulls for the desired size
        for (int i = 0; i < size; i++) {
            registers.add(null);
        }
    }

    public void load(Object obj, int index) {
        if (index < 0 || index >= registers.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        switch (obj) {
            case Byte b -> loadByte(b, index);
            case Integer i -> loadInt(i, index);
            case Long l -> loadLong(l, index);
            case Double d -> loadDouble(d, index);
            case String s -> loadString(s, index);
            default -> throw new IllegalArgumentException("Could not store " + obj.getClass().getSimpleName() + ", unknown type.");
        }
    }

    private void loadByte(byte b, int index) {
        registers.set(index, new Object[]{BYTE_TYPE, b}); // Store as an object array
    }

    private void loadInt(int i, int index) {
        if (index + 1 >= registers.size()) {
            throw new IndexOutOfBoundsException("Not enough space to store an integer at index: " + index);
        }
        registers.set(index, new Object[]{INT_TYPE, i}); // Store as an object array
    }

    private void loadLong(long l, int index) {
        if (index + 1 >= registers.size()) {
            throw new IndexOutOfBoundsException("Not enough space to store a long at index: " + index);
        }
        registers.set(index, new Object[]{LONG_TYPE, l}); // Store as an object array
    }

    private void loadDouble(double d, int index) {
        if (index + 1 >= registers.size()) {
            throw new IndexOutOfBoundsException("Not enough space to store a double at index: " + index);
        }
        registers.set(index, new Object[]{DOUBLE_TYPE, d}); // Store as an object array
    }

    private void loadString(String s, int index) {
        byte[] stringBytes = s.getBytes();
        if (index + 2 + stringBytes.length >= registers.size()) {
            throw new IndexOutOfBoundsException("Not enough space to store a string at index: " + index);
        }
        registers.set(index, new Object[]{STRING_TYPE, stringBytes}); // Store type and bytes
    }

    public Object get(int index) {
        if (index < 0 || index >= registers.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        Object[] typeIdentifierAndValue = (Object[]) registers.get(index);
        byte typeIdentifier = (byte) typeIdentifierAndValue[0];

        return switch (typeIdentifier) {
            case BYTE_TYPE -> getByte(typeIdentifierAndValue);
            case INT_TYPE -> getInt(typeIdentifierAndValue);
            case LONG_TYPE -> getLong(typeIdentifierAndValue);
            case DOUBLE_TYPE -> getDouble(typeIdentifierAndValue);
            case STRING_TYPE -> getString(typeIdentifierAndValue);
            default -> throw new IllegalArgumentException("Unknown type identifier: " + typeIdentifier);
        };
    }

    private byte getByte(Object[] typeIdentifierAndValue) {
        return (byte) typeIdentifierAndValue[1];
    }

    private int getInt(Object[] typeIdentifierAndValue) {
        return (int) typeIdentifierAndValue[1];
    }

    private long getLong(Object[] typeIdentifierAndValue) {
        return (long) typeIdentifierAndValue[1];
    }

    private double getDouble(Object[] typeIdentifierAndValue) {
        return (double) typeIdentifierAndValue[1];
    }

    private String getString(Object[] typeIdentifierAndValue) {
        byte[] stringBytes = (byte[]) typeIdentifierAndValue[1];
        return new String(stringBytes);
    }
}
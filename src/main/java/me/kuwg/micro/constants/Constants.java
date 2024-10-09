package me.kuwg.micro.constants;

import static me.kuwg.micro.constants.Constants.MemoryConstants.KILOBYTE;
import static me.kuwg.micro.constants.Constants.MemoryConstants.MEGABYTE;

public final class Constants extends ConstantClass {

    public static final class InstructionConstants extends ConstantClass {
        public static final byte LOAD = 0x00;
        public static final byte ADD = 0x01;
        public static final byte SUB = 0x02;
        public static final byte MUL = 0x03;
        public static final byte DIV = 0x04;
        public static final byte HALT = 0x05;
        public static final byte JEZ = 0x06;
        public static final byte JMZ = 0x07;
        public static final byte JLZ = 0x08;
        public static final byte JNZ = 0x09;
        public static final byte STORE = 0x0a;
        public static final byte CALL = 0x0b;
        public static final byte JUMP = 0X0c;
        public static final byte LOC = 0x0d;
        public static final byte FETCH = 0x0e;
        public static final byte JIT = 0x0f;
        public static final byte JIF = 0x12;
    }

    public static final class TypeConstants extends ConstantClass {
        public static final byte BYTE_TYPE = 0x01;
        public static final byte INT_TYPE = 0x02;
        public static final byte LONG_TYPE = 0x03;
        public static final byte DOUBLE_TYPE = 0x04;
        public static final byte STRING_TYPE = 0x05;
        public static final byte BOOLEAN_TYPE = 0x06;
    }

    public static final class HaltConstants extends ConstantClass {
        public static final byte DEFAULT_HALT = 0x00;  // halting without id
    }

    public static final class ValueDeclarationConstants extends ConstantClass {
        public static final byte REGISTER = 0x00;  // register value
        public static final byte VALUE = 0x01;  // register value
    }

    public static final class DefaultConstants extends ConstantClass {
        public static final int DEFAULT_MEMORY = (int) (2 * MEGABYTE); // default vm memory
        public static final int DEFAULT_REGISTERS = (int) (3 * KILOBYTE); // default vm registers
        public static final String DEFAULT_VM_THREAD_NAME = "MicroVM-main";
    }

    public static final class MemoryConstants extends ConstantClass {
        public static final long BYTE = 1L;
        private static final long MUL = 1024L;
        public static final long KILOBYTE = MUL * BYTE;
        public static final long MEGABYTE = MUL * KILOBYTE;
        public static final long GIGABYTE = MUL * MEGABYTE;
        public static final long TERABYTE = MUL * GIGABYTE;

        public static String toReadableSize(long bytes) {
            if (bytes >= TERABYTE) {
                return bytes / (double) TERABYTE + " Terabytes";
            } else if (bytes >= GIGABYTE) {
                return bytes / (double) GIGABYTE + " Gigabytes";
            } else if (bytes >= MEGABYTE) {
                return bytes / (double) MEGABYTE + " Megabytes";
            } else if (bytes >= KILOBYTE) {
                return bytes / (double) KILOBYTE + " Kilobytes";
            } else {
                return bytes + " Bytes";
            }
        }
    }

    public static final class BooleanConstants extends ConstantClass {
        public static final byte FALSE = 0x00;
        public static final byte TRUE = 0x01;
    }
}

abstract class ConstantClass {
    protected ConstantClass() {
        throw new RuntimeException("You cannot instantiate this class!");
    }
}
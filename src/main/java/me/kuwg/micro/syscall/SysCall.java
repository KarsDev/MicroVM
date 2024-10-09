package me.kuwg.micro.syscall;

import me.kuwg.micro.vm.MicroVirtualMachine;

public enum SysCall {
    PRINTLN("PRINTLN") {
        @Override
        public void handle(final MicroVirtualMachine vm, final int params) {
            if (params != 1) {
                throw new IllegalArgumentException("Expected 1 param in PRINTLN syscall, instead got " + params);
            }

            System.out.println(vm.readValue());
        }
    },
    ;

    public static final SysCall[] VALUES = values();

    private final String name;

    SysCall(String name) {
        this.name = name;
    }

    public static byte getByName(String name) {
        for (byte i = 0; i < VALUES.length; i++) {
            if (VALUES[i].name.equals(name)) {
                return i;
            }
        }

        throw new IllegalArgumentException("Unknown System Call: " + name);
    }

    public abstract void handle(MicroVirtualMachine vm, int params);
}

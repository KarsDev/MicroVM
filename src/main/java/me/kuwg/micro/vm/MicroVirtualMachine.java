package me.kuwg.micro.vm;

import me.kuwg.micro.bytecode.Bytecode;
import me.kuwg.micro.constants.Constants;
import me.kuwg.micro.syscall.SysCall;

import java.util.HashMap;
import java.util.Map;

import static me.kuwg.micro.constants.Constants.InstructionConstants.*;
import static me.kuwg.micro.constants.Constants.ValueDeclarationConstants.*;

import static me.kuwg.micro.util.OperationUtil.*;

public class MicroVirtualMachine {
    private transient final Bytecode bytecode;
    private transient final VirtualMemory registers;
    private transient final VirtualMemory memory;

    private transient final Map<Byte, Integer> locToReaderMap;

    private volatile transient boolean running;
    private volatile transient int status;

    public MicroVirtualMachine(int bcl, int mem, int reg) {
        this.bytecode = new Bytecode(bcl);
        this.memory = new VirtualMemory(mem);
        this.registers = new VirtualMemory(reg);

        this.locToReaderMap = new HashMap<>();

        this.running = false;
        this.status = 0;
    }

    public void load(final byte[] bytecode) {
        this.bytecode.load(bytecode);
    }

    public void start() {
        running = true;
        new VMRunner().start();
    }

    private void iLoad() {
        byte pointer = readByte(); // register to load into
        Object value = readValue();
        registers.store(pointer, value);
    }

    private void iAdd() {
        final Object left = readValue();
        final Object right = readValue();
        final byte pointer = readByte();
        registers.store(pointer, add(left, right));
    }

    private void iSub() {
        final Object left = readValue();
        final Object right = readValue();
        final byte pointer = readByte();
        registers.store(pointer, sub(left, right));
    }

    private void iMul() {
        final Object left = readValue();
        final Object right = readValue();
        final byte pointer = readByte();
        registers.store(pointer, mul(left, right));
    }

    private void iDiv() {
        final Object left = readValue();
        final Object right = readValue();
        final byte pointer = readByte();
        registers.store(pointer, div(left, right));
    }

    private void iHalt() {
        running = false;
        status = readIntValue();
    }

    private void iJEZ() {
        double eq = readNumberValue().doubleValue();

        byte jump = readByte();

        if (eq == 0) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    private void iJMZ() {
        double eq = readNumberValue().doubleValue();

        byte jump = readByte();

        if (eq > 0) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    private void iJLZ() {
        double eq = readNumberValue().doubleValue();

        byte jump = readByte();

        if (eq < 0) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    private void iJNZ() {
        double eq = readNumberValue().doubleValue();

        byte jump = readByte();

        if (eq != 0) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    private void iStore() {
        byte pointer = readByte();
        Object value = readValue();
        memory.store(pointer, value);
    }

    private void iCall() {
        byte syscallID = readByte();
        SysCall sysCall = SysCall.VALUES[syscallID];
        byte len = readByte();

        sysCall.handle(this, len);
    }

    private void iJump() {
        byte jump = readByteValue();
        bytecode.readerIndex(locToReaderMap.get(jump));
    }

    private void iLoc() {
        locToReaderMap.put(readByte(), bytecode.readerIndex());
    }

    private void iFetch() {
        byte pointer = readByte();
        byte register = readByte();
        Object result = memory.load(pointer);
        registers.store(register, result);
    }

    private void iJIT() {
        boolean eq = readBoolValue();

        byte jump = readByte();

        if (eq) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    private void iJIF() {
        boolean eq = readBoolValue();

        byte jump = readByte();

        if (!eq) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    public Object readValue() {
        final byte dType = readByte();

        if (dType == REGISTER) {
            return registers.load(readByte());
        }

        return bytecode.read();
    }

    private byte readInstructionByte() {
        try {
            return bytecode.readByte();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("Expected halting at the end of the file.");
        }
    }

    private byte readByte() {
        return bytecode.readByte();
    }

    public int readIntValue() {
        Object result = readValue();

        if (!(result instanceof Integer || result instanceof Byte || result instanceof Long)) {
            throw new RuntimeException("Expected int value, instead got " + result.getClass().getSimpleName());
        }

        return ((Number) result).intValue();
    }

    public byte readByteValue() {
        Object result = readValue();

        if (!(result instanceof Byte)) {
            throw new RuntimeException("Expected byte value, instead got " + result);
        }

        return ((Number) result).byteValue();
    }

    private Number readNumberValue() {
        Object result = readValue();

        if (!(result instanceof Number)) {
            throw new RuntimeException("Expected num value, instead got " + result);
        }

        return (Number) result;
    }

    private boolean readBoolValue() {
        Object result = readValue();

        if (!(result instanceof Boolean)) {
            throw new RuntimeException("Expected bool value, instead got " + result);
        }

        return (boolean) result;
    }


    public class VMRunner implements Runnable {

        protected void start() {
            new Thread(this, Constants.DefaultConstants.DEFAULT_VM_THREAD_NAME).start();
        }

        @Override
        public void run() {
            while (running) {
                byte instruction = readInstructionByte();
                switch (instruction) {
                    case LOAD: {
                        iLoad();
                        break;
                    }
                    case ADD: {
                        iAdd();
                        break;
                    }
                    case SUB: {
                        iSub();
                        break;
                    }
                    case MUL: {
                        iMul();
                        break;
                    }
                    case DIV: {
                        iDiv();
                        break;
                    }
                    case HALT: {
                        iHalt();
                        break;
                    }
                    case JEZ: {
                        iJEZ();
                        break;
                    }
                    case JMZ: {
                        iJMZ();
                        break;
                    }
                    case JLZ: {
                        iJLZ();
                        break;
                    }
                    case JNZ: {
                        iJNZ();
                        break;
                    }
                    case STORE: {
                        iStore();
                        break;
                    }
                    case CALL: {
                        iCall();
                        break;
                    }
                    case JUMP: {
                        iJump();
                        break;
                    }
                    case LOC: {
                        iLoc();
                        break;
                    }
                    case FETCH: {
                        iFetch();
                        break;
                    }
                    case JIT: {
                        iJIT();
                        break;
                    }
                    case JIF: {
                        iJIF();
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unknown instruction: " + instruction);
                    }
                }
            }

            System.exit(status);
        }
    }
}
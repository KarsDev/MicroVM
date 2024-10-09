package me.kuwg.micro.vm;

import me.kuwg.micro.bytecode.Bytecode;
import me.kuwg.micro.constants.Constants;
import me.kuwg.micro.syscall.SysCall;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static me.kuwg.micro.constants.Constants.InstructionConstants.*;
import static me.kuwg.micro.constants.Constants.ValueDeclarationConstants.*;

import static me.kuwg.micro.util.OperationUtil.*;

public class MicroVirtualMachine {
    private transient final Bytecode bytecode;
    private transient final MicroRegisters registers;
    private transient final MicroMemory memory;

    private transient final Map<Byte, Integer> locToReaderMap;

    private volatile transient boolean running;
    private volatile transient int status;

    public MicroVirtualMachine(int bcl, int mem, int reg) {
        this.bytecode = new Bytecode(bcl);
        this.memory = new MicroMemory(mem);
        this.registers = new MicroRegisters(reg);

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
        byte pointer = bytecode.readByte(); // register to load into
        Object value = readValue();
        registers.load(value, pointer);
    }

    private void iAdd() {
        final Object left = readValue();
        final Object right = readValue();
        final byte pointer = bytecode.readByte();
        registers.load(add(left, right), pointer);
    }

    private void iSub() {
        final Object left = readValue();
        final Object right = readValue();
        final byte pointer = bytecode.readByte();
        registers.load(sub(left, right), pointer);
    }

    private void iMul() {
        final Object left = readValue();
        final Object right = readValue();
        final byte pointer = bytecode.readByte();
        registers.load(mul(left, right), pointer);
    }

    private void iDiv() {
        final Object left = readValue();
        final Object right = readValue();
        final byte pointer = bytecode.readByte();
        registers.load(div(left, right), pointer);
    }

    private void iHalt() {
        running = false;
        status = readIntValue();
    }

    private void iJEZ() {
        double eq = readNumberValue().doubleValue();

        byte jump = readByteValue();

        if (eq == 0) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    private void iJMZ() {
        double eq = readNumberValue().doubleValue();

        byte jump = readByteValue();

        if (eq > 0) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    private void iJLZ() {
        double eq = readNumberValue().doubleValue();

        byte jump = readByteValue();

        if (eq < 0) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    private void iJNZ() {
        double eq = readNumberValue().doubleValue();

        byte jump = bytecode.readByte();

        if (eq != 0) {
            bytecode.readerIndex(locToReaderMap.get(jump));
        }
    }

    private void iStore() {
        byte pointer = bytecode.readByte();
        Object value = readValue();
        memory.store(pointer, value);
    }

    private void iCall() {
        byte syscallID = bytecode.readByte();
        SysCall sysCall = SysCall.VALUES[syscallID];
        byte len = bytecode.readByte();

        sysCall.handle(this, len);
    }

    private void iJump() {
        byte jump = readByteValue();
        bytecode.readerIndex(locToReaderMap.get(jump));
    }

    private void iLoc() {
        locToReaderMap.put(bytecode.readByte(), bytecode.readerIndex());
    }

    private void iFetch() {
        byte pointer = bytecode.readByte();
        byte register = bytecode.readByte();
        Object result = memory.load(pointer);
        registers.load(result, register);
    }


    public Object readValue() {
        final byte dType = bytecode.readByte();

        if (dType == REGISTER) {
            return registers.get(bytecode.readByte());
        }

        return bytecode.read();
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

    public class VMRunner implements Runnable {

        protected void start() {
            new Thread(this, Constants.DefaultConstants.DEFAULT_VM_THREAD_NAME).start();
        }

        @Override
        public void run() {
            while (running) {
                byte instruction = bytecode.readByte();
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
                    default: {
                        throw new RuntimeException("Unknown instruction: " + instruction);
                    }
                }
            }

            System.exit(status);
        }
    }
}

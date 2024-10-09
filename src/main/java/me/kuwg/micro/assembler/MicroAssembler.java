package me.kuwg.micro.assembler;

import me.kuwg.micro.syscall.SysCall;

import java.nio.ByteBuffer;
import java.util.*;

import static me.kuwg.micro.constants.Constants.BooleanConstants.FALSE;
import static me.kuwg.micro.constants.Constants.BooleanConstants.TRUE;
import static me.kuwg.micro.constants.Constants.HaltConstants.*;
import static me.kuwg.micro.constants.Constants.InstructionConstants.*;
import static me.kuwg.micro.constants.Constants.TypeConstants.*;
import static me.kuwg.micro.constants.Constants.ValueDeclarationConstants.*;

public class MicroAssembler {

    private static final Map<String, Byte> INSTRUCTION_SET = new HashMap<>();

    static {
        loadInstructionSet("load", LOAD);    // load <reg> <val>
        loadInstructionSet("add", ADD);      // add <a> <b> <result>
        loadInstructionSet("sub", SUB);      // subtract <a> <b> <result>
        loadInstructionSet("mul", MUL);      // multiply <a> <b> <result>
        loadInstructionSet("div", DIV);      // divide <a> <b> <result>
        loadInstructionSet("halt", HALT);    // halt
        loadInstructionSet("jez", JEZ);      // jump if 0 <reg> <loc>
        loadInstructionSet("jmz", JMZ);      // jump more 0 <reg> <loc>
        loadInstructionSet("jlz", JLZ);      // jump less 0 <reg> <loc>
        loadInstructionSet("jnz", JNZ);      // jump not 0 <reg> <loc
        loadInstructionSet("store", STORE);  // store <reg> <val>
        loadInstructionSet("call", CALL);    // call <SysCall> [params]
        loadInstructionSet("jump", JUMP);    // jump <loc>
        loadInstructionSet("fetch", FETCH);  // fetch <x> <reg>
    }

    private final String code;
    private final Map<String, Byte> locationMap = new HashMap<>();

    public MicroAssembler(String code) {
        this.code = code;
    }

    private static void loadInstructionSet(String name, int id) {
        INSTRUCTION_SET.put(name, (byte) id);
    }

    public byte[] assemble() {
        // First pass: collect labels
        String[] lines = code.split("\\n");
        List<Byte> byteCode = new ArrayList<>();
        byte currentIndex = 0;

        // First pass: Identify labels
        for (String line : lines) {
            int index = line.indexOf(";");

            if (index != -1) {
                line = line.substring(0, index);  // Remove comments
            }

            if (line.isEmpty() || line.isBlank()) {
                continue; // Skip empty lines
            }

            line = line.trim();

            if (line.endsWith(":")) {
                String label = line.substring(0, line.length() - 1); // Remove the colon from label
                locationMap.put(label, currentIndex); // Add label and its index to location map
                byteCode.add(LOC);
                byteCode.add(currentIndex);
                continue; // Skip label lines and move to the next line
            }

            // Parse instruction line
            byteCode.addAll(parseInstruction(line)); // Parse the instruction line
            currentIndex++; // Increment index for each instruction
        }

        // Second pass: Resolve labels and convert to bytecode
        byte[] byteArray = new byte[byteCode.size()];
        for (int i = 0; i < byteCode.size(); i++) {
            byteArray[i] = byteCode.get(i);
        }
        return byteArray;
    }

    private List<Byte> parseInstruction(String line) {
        String[] tokens = splitLine(line);

        if (tokens.length == 0) {
            throw new IllegalArgumentException("Unknown instruction: \"" + line + "\"");
        }

        String instruction = tokens[0];

        final List<Byte> bytes = new ArrayList<>();

        if (!INSTRUCTION_SET.containsKey(instruction)) {
            throw new IllegalArgumentException("Unknown instruction: \"" + instruction + "\"");
        }



        byte instructionByte = INSTRUCTION_SET.get(instruction);

        bytes.add(instructionByte);

        // Handle specific instruction formats based on the instruction
        switch (instruction) {
            case "store":
                // Expecting format: <byte> <val>
                bytes.add(parseByte(tokens[1]));
                bytes.addAll(parseValueOrRegister(tokens[2]));
                break;
            case "load":
                // Expecting format: <reg> <val>
                bytes.add(parseRegister(tokens[1]));
                bytes.addAll(parseValueOrRegister(tokens[2]));
                break;
            case "add":
            case "sub":
            case "mul":
            case "div":
                // Expecting format: <reg> <reg>
                bytes.addAll(parseValueOrRegister(tokens[1]));
                bytes.addAll(parseValueOrRegister(tokens[2]));
                bytes.add(parseRegister(tokens[3]));
                break;
            case "call":
                // Expecting format: <SysCall>
                bytes.add(SysCall.getByName(tokens[1]));
                List<Byte> params = new ArrayList<>();
                byte size = 0;
                for (int i = 2; i < tokens.length; i++) {
                    params.addAll(parseValueOrRegister(tokens[i]));
                    size ++;
                }
                bytes.add(size);
                bytes.addAll(params);
                break;

            case "jez":
            case "jmz":
            case "jlz":
            case "jnz":
                // Expecting format: <reg> <loc>
                bytes.addAll(parseValueOrRegister(tokens[1]));
                bytes.add(parseLocation(tokens[2]));
                break;
            case "jump":
                // Expecting format: jump <loc>
                bytes.add(parseLocation(tokens[1]));
                break;
            case "halt":
                if (tokens.length == 2) {
                    bytes.addAll(parseValueOrRegister(tokens[1]));
                } else {
                    bytes.add(VALUE);
                    bytes.add(BYTE_TYPE);
                    bytes.add(DEFAULT_HALT);
                }
                break;
            case "fetch":
                // Expected format: <byte> <reg>
                bytes.add(parseByte(tokens[1]));
                bytes.add(parseRegister(tokens[2]));
                break;
            default:
                throw new IllegalArgumentException("Unsupported instruction: " + instruction);
        }

        return bytes;
    }

    public static String[] splitLine(String input) {
        List<String> result = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
                currentToken.append(c);
            } else if (c == ' ' && !insideQuotes) {
                if (!currentToken.isEmpty()) {
                    result.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else {
                currentToken.append(c);
            }
        }

        if (!currentToken.isEmpty()) {
            result.add(currentToken.toString());
        }

        return result.toArray(new String[0]);
    }

    private List<Byte> parseValueOrRegister(String val) {
        final List<Byte> bytes = new ArrayList<>();

        if (val.startsWith("R")) {
            bytes.add(REGISTER);
            bytes.add(parseRegister(val));
        } else {
            bytes.add(VALUE);
            bytes.addAll(parseValue(val));
        }

        return bytes;
    }

    private byte parseRegister(String reg) {
        // Convert register name (e.g., "R0") to byte index
        // Assuming registers are named R0, R1, R2, etc.
        if (reg.startsWith("R")) {
            int regNum = Integer.parseInt(reg.substring(1));
            return (byte) regNum;
        }
        throw new IllegalArgumentException("Invalid register: " + reg);
    }

    private List<Byte> parseValue(String value) {
        List<Byte> byteList = new ArrayList<>();

        // Detect string type if value is enclosed in quotes
        if (value.startsWith("\"") && value.endsWith("\"")) {
            String str = value.substring(1, value.length() - 1);  // Remove quotes
            byteList.add(STRING_TYPE);  // Add string identifier

            byte[] stringBytes = str.getBytes();  // Convert string to bytes
            byteList.add((byte) stringBytes.length);  // Add string length
            for (byte b : stringBytes) {
                byteList.add(b);  // Add string content
            }
        }
        // Detect long integer type (use L suffix to distinguish from int)
        else if (value.endsWith("L") || value.endsWith("l")) {
            byteList.add(LONG_TYPE);  // Add long identifier

            long longValue = Long.parseLong(value.substring(0, value.length() - 1));
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(longValue);
            for (byte b : buffer.array()) {
                byteList.add(b);  // Add long content
            }
        }
        // Detect double type (use D suffix to distinguish from int)
        else if (value.endsWith("D") || value.endsWith("d")) {
            byteList.add(DOUBLE_TYPE);  // Add double identifier

            double doubleValue = Double.parseDouble(value.substring(0, value.length() - 1));
            ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
            buffer.putDouble(doubleValue);
            for (byte b : buffer.array()) {
                byteList.add(b);  // Add double content
            }
        }
        // Detect 'false' and save it as boolean
        else if (value.equals("false")) {
            byteList.add(BOOLEAN_TYPE);
            byteList.add(FALSE);
        }
        // Detect 'true' and save it as boolean
        else if (value.equals("true")) {
            byteList.add(BOOLEAN_TYPE);
            byteList.add(TRUE);
        }
        // Default type, int or byte
        else {
            try {
                byte b = parseByte(value);
                byteList.add(BYTE_TYPE);  // Add byte identifier
                byteList.add(b);  // Add byte content
            } catch (NumberFormatException e) {
                int i  = Integer.parseInt(value);
                byteList.add(INT_TYPE);  // Add int identifier
                ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
                buffer.putInt(i);
                for (byte b : buffer.array()) {
                    byteList.add(b);  // Add double content
                }
            }


        }

        return byteList;
    }

    private byte parseLocation(String loc) {
        // Look up the location in the loc map
        Byte index = locationMap.get(loc);
        if (index == null) {
            throw new IllegalArgumentException("Unknown location: " + loc);
        }
        return index;
    }

    public static byte parseByte(String input) {
        input = input.trim();

        if (input.toLowerCase().startsWith("0x")) {
            String hexValue = input.substring(2);
            return (byte) Integer.parseInt(hexValue, 16);
        } else {
            return Byte.parseByte(input);
        }
    }
}
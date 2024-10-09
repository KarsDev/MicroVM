package me.kuwg.micro;

import me.kuwg.micro.assembler.MicroAssembler;
import me.kuwg.micro.vm.MicroVirtualMachine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static me.kuwg.micro.constants.Constants.DefaultConstants.*;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            printHelp();
            System.exit(1);
        }

        String command = args[0];

        switch (command.toLowerCase()) {
            case "interpret": {
                if (args.length < 2) {
                    System.err.println("Please provide an input file for interpretation.");
                    System.exit(1);
                }
                interpret(args[1]);
                break;
            }
            case "compile": {
                if (args.length < 2) {
                    System.err.println("Please provide an input file for compilation.");
                    System.exit(1);
                }
                String outputFile = args.length > 2 ? args[2] : getDefaultOutputFileA(args[1]);
                compile(args[1], outputFile);
                break;
            }
            case "run":{
                if (args.length < 2) {
                    System.err.println("Please provide an input .masm file to run.");
                    System.exit(1);
                }
                run(args[1]);
                break;
            }
            case "help": {
                printHelp();
                break;
            }
            default: {
                System.err.println("Unknown command: " + command);
                printHelp();
                System.exit(1);
            }
        }
    }

    private static void interpret(String inputFile) {
        try {
            byte[] bytecode = Files.readAllBytes(Paths.get(inputFile));

            MicroVirtualMachine machine = new MicroVirtualMachine(bytecode.length, DEFAULT_MEMORY, DEFAULT_REGISTERS);
            machine.load(bytecode);
            machine.start();
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void compile(String inputFile, String outputFile) {
        try {
            String program = Files.readString(Paths.get(inputFile));
            byte[] bytecode = new MicroAssembler(program).assemble();
            Files.write(Paths.get(outputFile), bytecode, StandardOpenOption.CREATE);
            System.out.println("Compilation successful. Output written to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error reading or writing the file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run(String inputFile) {
        try {
            String program = Files.readString(Paths.get(inputFile));
            byte[] bytecode = new MicroAssembler(program).assemble();

            MicroVirtualMachine machine = new MicroVirtualMachine(bytecode.length, DEFAULT_MEMORY, DEFAULT_REGISTERS);
            machine.load(bytecode);
            machine.start();
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String getDefaultOutputFileA(String inputFile) {
        if (inputFile.contains(".") && !inputFile.endsWith(".mc")) {
            return inputFile.substring(0, inputFile.indexOf(".")) + ".mc";
        } else {
            return inputFile + ".mc";
        }
    }

    private static String getDefaultOutputFileD(String inputFile) {
        if (inputFile.contains(".") && !inputFile.endsWith(".masm")) {
            return inputFile.substring(0, inputFile.indexOf(".")) + ".masm";
        } else {
            return inputFile + ".masm";
        }
    }


    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("  interpret <input>: Load and execute the specified input file.");
        System.out.println("  compile <input> [output]: Assemble the input file and save the bytecode to the output file.");
        System.out.println("  run <input.masm>: Load and execute the specified .masm file.");
        System.out.println("  help: Display this help message.");
    }
}
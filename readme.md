# MicroAssembly Assembly Guide

## Overview
MicroAssembly assembly is a multi-step process that transforms assembly code into machine-readable bytecode. This guide outlines the key phases involved in the assembly process.

## 1. Input Conversion
The assembly process begins by reading an input file containing assembly code. This file is converted into a string format, where each line represents either an instruction or a label in the assembly code.

## 2. MicroAssembler Class Overview
The `MicroAssembler` class serves as the core of the assembly process. It is initialized with the content of the assembly code and processes each line. The class maintains an instruction set and a mapping of labels to their respective locations in the bytecode.

## 3. Instruction Set Initialization
Before the assembly starts, the instruction set is established. This set consists of various assembly instructions, each associated with a unique opcode. Instructions such as `add`, `sub`, `mul`, `load`, and others are mapped to their respective bytecode representations for generating the final bytecode.

## 4. Two-Pass Assembly Process
The assembly process is carried out in two main passes:

### Pass 1: Label Collection
During the first pass, the assembler scans each line of the code to identify labels. Labels are identifiers marking specific locations in the code, allowing for easier navigation and reference during execution, especially for jump and branch instructions. When a label is encountered, its name and corresponding position in the bytecode are recorded in a location map.

### Pass 2: Instruction Parsing
In the second pass, the assembler processes the actual instructions. Each line of assembly is broken down into tokens, with the first token identified as the instruction. The assembler checks the instruction against the initialized instruction set and generates the appropriate bytecode by parsing the operands and parameters.

## 5. Instruction Parsing and Bytecode Generation
Each instruction has a specific format dictating how operands are handled. Below are detailed examples of how various types of instructions are processed:

### Load Instruction
**Example:** `load R1 42`  
**Description:** The load instruction retrieves a value (in this case, 42) and places it into the specified register (here, R1).  
**Bytecode Representation:** The assembler generates bytecode that includes the opcode for load, followed by the byte representation for the register and the value.

### Arithmetic Instructions
Arithmetic instructions such as `add`, `sub`, `mul`, and `div` require two source registers and a destination register.

- **Example for Add:** `add R1 R2 R3`  
  **Description:** This instruction adds the values in R1 and R2, storing the result in R3.  
  **Bytecode Representation:** The assembler processes this instruction by translating it into the respective opcode and the byte representation of the involved registers.

- **Example for Subtract:** `sub R3 R1 R2`  
  **Description:** This instruction subtracts the value in R1 from R3 and stores the result in R2.  
  **Bytecode Representation:** Similar to the add instruction, the assembler generates the appropriate bytecode.

- **Example for Multiply:** `mul R2 R3 R1`  
  **Description:** This instruction multiplies the values in R2 and R3, placing the result in R1.  
  **Bytecode Representation:** The corresponding opcode and register representations are included in the bytecode.

- **Example for Divide:** `div R1 R2 R3`  
  **Description:** This instruction divides the value in R1 by that in R2 and stores the result in R3.  
  **Bytecode Representation:** The assembler handles it similarly to other arithmetic instructions.

### Jump Instructions
Jump instructions dictate control flow based on conditions or specific target locations.

- **Example for Jump:** `jump label1`  
  **Description:** This unconditional jump instruction transfers control to the code at label1.  
  **Bytecode Representation:** The assembler replaces label1 with its corresponding bytecode location from the label map.

- **Example for Jump if Zero (jez):** `jez R1 label2`  
  **Description:** This instruction jumps to label2 if the value in R1 is zero.  
  **Bytecode Representation:** The assembler processes the instruction and generates bytecode based on the value of R1 and the label.

- **Example for Jump if Not Zero (jnz):** `jnz R2 label3`  
  **Description:** This instruction jumps to label3 if the value in R2 is not zero.  
  **Bytecode Representation:** Similar handling as the jez instruction, but the condition is inverted.

- **Example for Jump if Less than Zero (jlz):** `jlz R1 label4`  
  **Description:** This instruction jumps to label4 if the value in R1 is less than zero.  
  **Bytecode Representation:** The assembler converts this instruction into bytecode using the relevant condition and label.

### Call Instruction
**Example:** `call PRINTLN "Hello World"`  
**Description:** This instruction calls the PRINTLN system call, passing "Hello World" as a parameter.  
**Bytecode Representation:** The assembler parses the system call name and parameters, generating the corresponding bytecode for the call.

### Store Instruction
**Example:** `store R1 100`  
**Description:** This instruction stores the value from R1 into memory at address 100.  
**Bytecode Representation:** The assembler generates the bytecode for the store operation, including the register and the memory address.

### Fetch Instruction
**Example:** `fetch 100 R2`  
**Description:** This instruction retrieves the value stored at memory address 100 and loads it into R2.  
**Bytecode Representation:** The assembler generates bytecode that indicates the fetch operation, including the memory address and target register.

### Halt Instruction
**Example:** `halt`  
**Description:** This instruction terminates the program execution.  
**Bytecode Representation:** The assembler produces the bytecode for the halt operation, signifying that execution should stop. It may also handle any optional parameters specified.

## 6. Bytecode Conversion
After processing all instructions, the assembler converts the collected instructions and label references into an array of bytes. This byte array represents the final machine code that can be executed by the MicroAssembly runtime.

## 7. Value and Register Parsing
The assembler includes a mechanism for parsing both values (constants) and registers. Depending on the operand's format (whether it is a register identifier or a constant value), the assembler generates the appropriate bytecode representation, ensuring accurate processing.

## 8. Conclusion
The MicroAssembler uses a structured two-pass approach to convert MicroAssembly code into machine-readable bytecode. By first collecting labels and then resolving instructions, the assembler ensures that the resulting bytecode is accurate and executable, effectively translating high-level assembly instructions into low-level machine operations.
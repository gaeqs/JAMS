/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.instruction.basic;

import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.alu.ALUType;
import net.jamsimulator.jams.mips.instruction.execution.InstructionExecution;
import net.jamsimulator.jams.mips.instruction.execution.InstructionExecutionBuilder;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

import java.util.*;

/**
 * Represents a basic instruction. Basic instructions are {@link Instruction}s that have
 * a direct translation to MIPS machine code.
 */
public abstract class BasicInstruction<Inst extends AssembledInstruction> implements Instruction {

    private final String mnemonic;
    private final int operationCode;
    private final ALUType aluType;
    private final ParameterType[] parameters;
    private final Map<Architecture, InstructionExecutionBuilder<? extends Architecture, Inst>> executionBuilders;


    /**
     * Creates a basic instruction using a mnemonic, a parameter types array and an operation code.
     *
     * @param mnemonic      the mnemonic.
     * @param parameters    the parameter types.
     * @param aluType       the type of the ALU where this instruction will be executed.
     * @param operationCode the operation code.
     */
    public BasicInstruction(String mnemonic, ParameterType[] parameters, ALUType aluType, int operationCode) {
        this.mnemonic = mnemonic;
        this.parameters = parameters;
        this.aluType = aluType;
        this.operationCode = operationCode;
        this.executionBuilders = new HashMap<>();
    }

    /**
     * Creates a basic instruction using a mnemonic, a parameter types array and an operation code.
     *
     * @param mnemonic      the mnemonic.
     * @param parameters    the parameter types.
     * @param aluType       the type of the ALU where this instruction will be executed.
     * @param operationCode the operation code.
     */
    public BasicInstruction(String mnemonic, InstructionParameterTypes parameters, ALUType aluType, int operationCode) {
        this(mnemonic, parameters.getParameters(), aluType, operationCode);
    }


    @Override
    public String getName() {
        var sufix = mnemonic.toUpperCase().replace('.', '_');
        return Manager.ofS(Language.class).getSelected().getOrDefault("INSTRUCTION_" + sufix);
    }

    @Override
    public String getDocumentation() {
        var sufix = mnemonic.toUpperCase().replace('.', '_');
        return Manager.ofS(Language.class).getSelected().getOrDefault("INSTRUCTION_" + sufix + "_DOCUMENTATION");
    }

    @Override
    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public ParameterType[] getParameters() {
        return Arrays.copyOf(parameters, parameters.length);
    }

    @Override
    public boolean hasParameters() {
        return parameters.length > 0;
    }

    @Override
    public boolean match(String mnemonic, ParameterType[] parameters) {
        return this.mnemonic.equals(mnemonic) && Arrays.equals(this.parameters, parameters);
    }

    public <Arch extends Architecture> void addExecutionBuilder(Arch architecture, InstructionExecutionBuilder<Arch, Inst> builder) {
        executionBuilders.put(architecture, builder);
    }

    @Override
    public boolean match(String mnemonic, List<ParameterType>[] parameters) {
        if (!this.mnemonic.equalsIgnoreCase(mnemonic)) return false;
        if (parameters.length != this.parameters.length) return false;
        int i = 0;
        for (List<ParameterType> possibilities : parameters) {
            if (!possibilities.contains(this.parameters[i])) return false;
            i++;
        }
        return true;
    }

    @Override
    public AssembledInstruction[] assemble(InstructionSet set, int address, ParameterParseResult[] parameters) {
        return new AssembledInstruction[]{assembleBasic(parameters)};
    }

    /**
     * Returns the operation code of the instruction. This operation code is used to search
     * the instruction based on an instruction code.
     *
     * @return the operation code.
     */
    public int getOperationCode() {
        return operationCode;
    }

    /**
     * Returns the default {@link ALUType} of this instruction.
     * <p>
     * In architures with different ALU types, this class indicates
     * the ALU where this instruction will be executed.
     *
     * @return the {@link  ALUType}.
     */
    public ALUType getDefaultALUType() {
        return aluType;
    }

    /**
     * Returns whether this instruction matches the given instruction code.
     *
     * @param instructionCode the instruction code.
     * @return whether this instruction matches.
     */
    public boolean match(int instructionCode) {
        return (instructionCode >>> AssembledInstruction.OPERATION_CODE_SHIFT) == operationCode;
    }

    /**
     * Compiles the basic instruction using the given parameters.
     *
     * @param parameters the parameters.
     * @return the {@link AssembledInstruction}.
     */
    public final AssembledInstruction assembleBasic(ParameterParseResult[] parameters) {
        return assembleBasic(parameters, this);
    }

    /**
     * Compiles the basic instruction using the given parameters.
     *
     * @param parameters the parameters.
     * @param origin     the origin instruction. This may be a pseudo-instruction or this basic instruction.
     * @return the {@link AssembledInstruction}.
     */
    public abstract AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin);

    /**
     * Assembles the basic instruction using the given instruction code.
     *
     * @param instructionCode the instruction code.
     * @return the {@link AssembledInstruction}.
     */
    public abstract AssembledInstruction assembleFromCode(int instructionCode);

    /**
     * Generates a {@link InstructionExecution} that matches the given {@link Architecture}.
     *
     * @param simulation  the simulation.
     * @param instruction the assembled instruction.
     * @param address     the address of the instruction.
     * @param <Arch>      the architecture type.
     * @return the {@link InstructionExecution}.
     */
    @SuppressWarnings("unchecked")
    public <Arch extends Architecture> Optional<InstructionExecution<Arch, Inst>> generateExecution(MIPSSimulation<Arch> simulation, AssembledInstruction instruction, int address) {
        InstructionExecutionBuilder<Arch, Inst> fun = (InstructionExecutionBuilder<Arch, Inst>) executionBuilders.get(simulation.getArchitecture());
        if (fun == null) return Optional.empty();
        InstructionExecution<Arch, Inst> execution = fun.create(simulation, (Inst) instruction, address);
        return Optional.ofNullable(execution);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicInstruction<?> that = (BasicInstruction<?>) o;
        return mnemonic.equals(that.mnemonic) &&
                Arrays.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mnemonic);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }
}

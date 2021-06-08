package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.SimulationData;

import java.util.Map;
import java.util.Set;

/**
 * Represents an assembler. An assembler transforms assembly code into machine code.
 * <p>
 * To use a implementation of this class you must invoke {@link #assemble()} to assemble the code.
 * Then, invoke {@link #createSimulation(Architecture, SimulationData)} to create a {@link MIPSSimulation}.
 */
public interface Assembler {

    /**
     * Assembles the code. This may fail if the code was already assembled or the code has any error.
     *
     * @throws net.jamsimulator.jams.mips.assembler.exception.AssemblerException when the code is already assembled or the code has any error.
     */
    void assemble();

    /**
     * Returns whether the code inside this assembler has been assembled.
     *
     * @return whether the code inside this assembler has been assembled.
     */
    boolean isAssembled();

    /**
     * Creates a {@link MIPSSimulation} that matches the given {@link Architecture}.
     *
     * @param architecture the {@link Architecture}.
     * @param data         all the data required for the simulation to be built.
     * @param <Arch>       the architecture type.
     * @return the {@link MIPSSimulation}.
     * @throws IllegalStateException when the code is not assembled.
     */
    <Arch extends Architecture> MIPSSimulation<Arch> createSimulation(Arch architecture, SimulationData data);

    /**
     * Returns the {@link InstructionSet} used by this assembler.
     *
     * @return the {@link InstructionSet}.
     */
    InstructionSet getInstructionSet();

    /**
     * Returns the {@link DirectiveSet} used by this assembler.
     *
     * @return the {@link DirectiveSet}.
     */
    DirectiveSet getDirectiveSet();

    /**
     * Returns the {@link Registers register set} used by this assembler.
     * {@link MIPSSimulation}s created by this assembler will use a copy of these registers.
     *
     * @return the {@link Registers register set}.
     */
    Registers getRegisters();

    /**
     * Returns the {@link Memory} used by this assembler.
     * <p>
     * This memory should be an empty memory when the code is not assembled and a memory
     * with data and the assembled code when the code is assembled.
     * <p>
     * {@link MIPSSimulation}s created by this assembler will use a copy of this memory.
     *
     * @return the {@link Memory}.
     */
    Memory getMemory();

    /**
     * Returns an unmodifiable map that links instruction addresses to their original instructions.
     * If a pseudo instruction creates several instructions only the first one will be found inside this map.
     *
     * @return the unmodifiable map.
     */
    Map<Integer, String> getOriginals();

    /**
     * Returns an unmodifiable {@link Set} with all labels registered in this assembler code.
     *
     * @return the unmodifiable {@link Set}.
     */
    Set<Label> getAllLabels();

}

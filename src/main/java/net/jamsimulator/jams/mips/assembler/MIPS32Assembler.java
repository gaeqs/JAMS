package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.gui.util.log.Log;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.SimulationData;

import java.util.*;

/**
 * Represents a MIPS32 assembler.
 */
public class MIPS32Assembler implements Assembler {

    private final List<MIPS32AssemblingFile> files;
    private final MIPS32AssemblerData assemblerData;

    private final InstructionSet instructionSet;
    private final DirectiveSet directiveSet;
    private final Registers registers;
    private final Memory memory;

    private final Map<String, Integer> globalLabels;
    private final Map<Integer, String> originalInstructions;

    private final Log log;

    private boolean assembled = false;

    public MIPS32Assembler(Map<String, String> rawFiles, InstructionSet instructionSet, DirectiveSet directiveSet, Registers registers, Memory memory, Log log) {
        this.files = new ArrayList<>();

        this.assemblerData = new MIPS32AssemblerData(memory);

        this.instructionSet = instructionSet;
        this.directiveSet = directiveSet;
        this.registers = registers;
        this.memory = memory;

        this.globalLabels = new HashMap<>();
        this.originalInstructions = new HashMap<>();

        this.log = log;

        rawFiles.forEach((name, data) -> files.add(new MIPS32AssemblingFile(name, data, this)));
    }

    /**
     * Returns the {@link Log} this assembler used to inform about errors and warnings.
     *
     * @return the {@link Log}.
     */
    public Log getLog() {
        return log;
    }

    /**
     * Links the original instruction to the address its first compiled instruction is located.
     *
     * @param line    the line of the instruction.
     * @param address the address.
     * @param string  the instruction.
     * @see #getOriginals()
     */
    public void addOriginalInstruction(int line, int address, String string) {
        originalInstructions.put(address, (line + 1) + ": \t" + string);
    }

    /**
     * Adds the given label as a global label.
     *
     * @param executingLine the line executing this command.
     * @param label         the label to register.
     * @param address       the address of the label.
     */
    public void addGlobalLabel(int executingLine, String label, int address) {
        if (globalLabels.containsKey(label)) {
            throw new AssemblerException(executingLine, "The global label " + label + " is already defined.");
        }

        if (files.stream().anyMatch(target -> target.getLocalLabelAddress(label).isPresent())) {
            throw new AssemblerException(executingLine, "The label " + label +
                    " cannot be converted to a global label because there are two or more files with the same label.");
        }
        globalLabels.put(label, address);
    }

    /**
     * Returns the address that matches the given global label, if present.
     *
     * @param label the label.
     * @return the address, if present.
     */
    public OptionalInt getGlobalLabelAddress(String label) {
        return globalLabels.containsKey(label) ? OptionalInt.of(globalLabels.get(label)) : OptionalInt.empty();
    }

    /**
     * Returns the {@link MIPS32AssemblerData} of this assembler.
     *
     * @return the {@link MIPS32AssemblerData}.
     */
    public MIPS32AssemblerData getAssemblerData() {
        return assemblerData;
    }

    @Override
    public boolean isAssembled() {
        return assembled;
    }

    @Override
    public <Arch extends Architecture> Simulation<Arch> createSimulation(Arch architecture, SimulationData data) {
        if (!assembled) throw new IllegalStateException("The program is still not assembled!");

        Memory memory = getMemory().copy();
        memory.saveState();

        memory.restoreSavedState();

        Registers registers = getRegisters().copy();

        Simulation<?> simulation = architecture.createSimulation(instructionSet, registers, memory,
                assemblerData.getCurrentText() - 4,
                assemblerData.getCurrentKText() - 4, data);
        return (Simulation<Arch>) simulation;
    }

    @Override
    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    @Override
    public DirectiveSet getDirectiveSet() {
        return directiveSet;
    }

    @Override
    public Registers getRegisters() {
        return registers;
    }

    @Override
    public Memory getMemory() {
        return memory;
    }

    @Override
    public Map<Integer, String> getOriginals() {
        return Collections.unmodifiableMap(originalInstructions);
    }

    @Override
    public Map<String, Integer> getLabelsWithFileNames() {
        var map = new HashMap<>(globalLabels);
        files.forEach(file -> file.getLabels().forEach((label, address) ->
                map.put(file.getName() + "/" + label, address)));

        return Collections.unmodifiableMap(map);
    }

    @Override
    public void assemble() {
        if (assembled) throw new AssemblerException("The code is already assembled!");

        files.forEach(MIPS32AssemblingFile::scan);
        files.forEach(file -> {
            file.assembleInstructions();
            file.executeLabelRequiredDirectives();
        });

        //Reserves static memory.
        memory.allocateMemory(assemblerData.getCurrentData() - assemblerData.getFirstData());
        assembled = true;

        //Reset all caches.
        Optional<Memory> current = Optional.of(memory);
        while (current.isPresent()) {
            if (current.get() instanceof Cache) {
                ((Cache) current.get()).resetCache();
            }
            current = current.get().getNextLevelMemory();
        }

        int main = getGlobalLabelAddress("main").orElse(-1);
        if (main != -1) registers.getProgramCounter().setValue(main);

        registers.saveState();
    }
}

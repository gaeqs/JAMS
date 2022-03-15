/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.gui.util.log.Log;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationData;
import net.jamsimulator.jams.utils.RawFileData;

import java.util.*;

public class MIPS32Assembler implements Assembler {

    private final InstructionSet instructionSet;
    private final DirectiveSet directiveSet;
    private final Registers registers;
    private final Memory memory;
    private final Log log;

    private final List<MIPS32AssemblerFile> files = new ArrayList<>();
    private final Map<Integer, String> originalInstructions = new HashMap<>();
    private final MIPS32AssemblerScope globalScope = new MIPS32AssemblerScope("global", null, null);

    private final MIPS32AssemblerData assemblerData;

    private boolean assembled = false;

    public MIPS32Assembler(
            Iterable<RawFileData> rawFiles,
            InstructionSet instructionSet,
            DirectiveSet directiveSet,
            Registers registers,
            Memory memory,
            Log log
    ) {
        this.instructionSet = instructionSet;
        this.directiveSet = directiveSet;
        this.registers = registers;
        this.memory = memory;
        this.log = log;

        this.assemblerData = new MIPS32AssemblerData(memory);

        rawFiles.forEach(it -> files.add(new MIPS32AssemblerFile(this, it.file(), it.data())));
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

    public Log getLog() {
        return log;
    }

    @Override
    public Map<Integer, String> getOriginals() {
        return originalInstructions;
    }

    @Override
    public Set<Label> getAllLabels() {
        return new HashSet<>(globalScope.getScopeLabels().values());
    }

    @Override
    public int getStackBottom() {
        return assemblerData.getCurrentText() - 4;
    }

    @Override
    public int getKernelStackBottom() {
        return assemblerData.getCurrentKText() - 4;
    }

    @Override
    public OptionalInt getStartAddres() {
        Label label = globalScope.getScopeLabels().get("main");
        if (label == null) return OptionalInt.empty();
        return OptionalInt.of(label.getAddress());
    }

    public MIPS32AssemblerData getAssemblerData() {
        return assemblerData;
    }

    @Override
    public boolean isAssembled() {
        return assembled;
    }

    @Override
    public <Arch extends Architecture> MIPSSimulation<Arch> createSimulation(Arch architecture, MIPSSimulationData data) {
        if (!assembled) throw new IllegalStateException("The program is still not assembled!");
        MIPSSimulation<?> simulation = architecture.createSimulation(data);
        return (MIPSSimulation<Arch>) simulation;
    }

    public MIPS32AssemblerScope getGlobalScope() {
        return globalScope;
    }

    // region log utils

    public void printInfo(Object info) {
        if (log != null) log.printInfoLn(info);
    }

    public void printDone(Object done) {
        if (log != null) log.printDoneLn(done);
    }

    public void printWarning(Object warning) {
        if (log != null) log.printWarningLn(warning);
    }

    public void printError(Object error) {
        if (log != null) log.printErrorLn(error);
    }

    // endregion

    @Override
    public void assemble() {
        if (assembled) throw new AssemblerException("Project is already assembled");
        printInfo("Discovering...");
        files.forEach(MIPS32AssemblerFile::discoverElements);
        printInfo("Expanding macros...");
        files.forEach(MIPS32AssemblerFile::expandMacros);
        printInfo("Assigning addresses...");
        files.forEach(MIPS32AssemblerFile::assignAddresses);
        printInfo("Assigning values...");
        files.forEach(MIPS32AssemblerFile::assignValues);
        assembled = true;
    }
}

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
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Gael Rial Costas's MIPS32 assembler.
 * <p>
 * This assembler assembles the given code in four steps.
 * <ul>
 *     <li>
 *      <strong>Step 1</strong>: discovering. In this step lines are split into their primitive elements.
 *      Global and file labels are registered without their values.
 *     </li>
 *     <li>
 *      <strong>Step 2</strong>: macro execution. Macros are invoked and their result is placed in the corresponding position.
 *      Added lines execute the step 1.
 *     </li>
 *     <li>
 *      <strong>Step 3</strong>: address assignation: addresses are assigned to labels, instructions and directives.
 *     </li>
 *     <li>
 *      <strong>Step 4</strong>: values assignation: in this step the values of labels are known.
 *      Instructions and directives write their corresponding values into memory.
 *     </li>
 * </ul>
 * <p>
 *
 * <h2>Labels</h2>
 * There are several limitations to the names of labels:
 * <ul>
 *     <li>
 *         The name cannot contain the following characters: <strong>\ ; " # '</strong>
 *     </li>
 *     <li>
 *         The name cannot contain the character <strong>:</strong> alone. It may contain the string <strong>::</strong>
 *     </li>
 *     <li>
 *         The name cannot be '+' or '-'. These are relative label references and their names are reserved.
 *     </li>
 * </ul>
 * <p>
 * To declare a label, type the name and a <strong>:</strong> st the start of the line: '<strong>key:</strong>'
 * <p>
 * You can also declare a label using the directive <strong>.lab</strong>.
 * <p>
 * There are two special label names: '-' and '+'. There are relative label references. They reference
 * the previous or next label in the <strong>same scope</strong>.
 *
 * <h2>Macros and scopes</h2>
 * You can define a macro using the directives <strong>.macro</strong> and <strong>.endmacro</strong>.
 * The code between these directives will be transformed into a macro.
 * <p>
 * When a macro is called, a new local scope is created. Elements can reference labels and macros from this scope
 * or a parent scope.
 * <p>
 * Labels and macros defined in a scope can only be referenced by elements of the same scope or a child scope.
 *
 * @author Gael Rial Costas
 * @version 2.0
 */
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

    /**
     * Creates a MIPS32 assembler.
     * <p>
     * This construction only setups the assembler.
     * Use {@link #assemble()} to assemble.
     *
     * @param rawFiles       the files to assemble.
     * @param instructionSet the instruction set to use.
     * @param directiveSet   the directive set to use.
     * @param registers      the registers to use.
     * @param memory         the memory to use.
     * @param log            the log used to output messages. It can be null.
     */
    public MIPS32Assembler(
            Iterable<RawFileData> rawFiles,
            InstructionSet instructionSet,
            DirectiveSet directiveSet,
            Registers registers,
            Memory memory,
            Log log
    ) {
        Validate.notNull(rawFiles, "Raw files cannot ben null!");
        Validate.notNull(instructionSet, "Instruction set cannot be null!");
        Validate.notNull(directiveSet, "Directive set cannot be null!");
        Validate.notNull(registers, "Registers cannot be null!");
        Validate.notNull(memory, "Memory cannot be null!");
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
        printInfo("Adding final touches...");

        var startAddress = getStartAddres();
        if (startAddress.isPresent()) {
            registers.getProgramCounter().setValue(startAddress.getAsInt());
        } else {
            printWarning("Global label 'main' not found. Execution will start at the start of the text section.");
        }

        memory.allocateMemory(memory.getFirstDataAddress() - assemblerData.getCurrentData());

        assembled = true;
    }

    /**
     * Returns the assembler's global data.
     *
     * @return the assembler's global data.
     */
    public MIPS32AssemblerData getAssemblerData() {
        return assemblerData;
    }

    /**
     * Returns the {@link MIPS32AssemblerScope global scope} of this assembler.
     *
     * @return the {@link MIPS32AssemblerScope global scope}.
     */
    public MIPS32AssemblerScope getGlobalScope() {
        return globalScope;
    }

    // region log utils

    /**
     * Prints an info message on the log.
     * This method does nothing if the log is null.
     *
     * @param info the message.
     */
    public void printInfo(Object info) {
        if (log != null) log.printInfoLn(info);
    }

    /**
     * Prints a done message on the log.
     * This method does nothing if the log is null.
     *
     * @param done the message.
     */
    public void printDone(Object done) {
        if (log != null) log.printDoneLn(done);
    }

    /**
     * Prints a warning message on the log.
     * This method does nothing if the log is null.
     *
     * @param warning the message.
     */
    public void printWarning(Object warning) {
        if (log != null) log.printWarningLn(warning);
    }

    /**
     * Prints an error message on the log.
     * This method does nothing if the log is null.
     *
     * @param error the message.
     */
    public void printError(Object error) {
        if (log != null) log.printErrorLn(error);
    }

    // endregion
}

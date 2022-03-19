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

package net.jamsimulator.jams.utils;

import net.jamsimulator.jams.gui.util.log.PrintStreamLog;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.assembler.MIPS32Assembler;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.directive.set.MIPS32DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.instruction.set.MIPS32r6InstructionSet;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationData;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationSource;
import net.jamsimulator.jams.project.mips.configuration.MIPSSimulationConfiguration;

import java.io.File;
import java.util.Set;

public class TestUtils {

    private static final MIPSSimulationConfiguration CONFIG = new MIPSSimulationConfiguration("test");
    private static final InstructionSet INSTRUCTION_SET = new MIPS32r6InstructionSet(ResourceProvider.JAMS);
    private static final DirectiveSet DIRECTIVE_SET = new MIPS32DirectiveSet(ResourceProvider.JAMS);

    public static void assemble(String text) {
        var registers = new MIPS32Registers();
        var memory = new MIPS32Memory();
        var rawFiles = new RawFileData("test", text);

        var assembler = new MIPS32Assembler(
                Set.of(rawFiles),
                INSTRUCTION_SET,
                DIRECTIVE_SET,
                registers,
                memory,
                new PrintStreamLog(System.out)
        );
        assembler.assemble();
    }

    public static MIPSSimulation<?> generateSimulation(Architecture architecture, String text) {
        var registers = new MIPS32Registers();
        var memory = new MIPS32Memory();
        var rawFiles = new RawFileData("test", text);

        var assembler = new MIPS32Assembler(
                Set.of(rawFiles),
                INSTRUCTION_SET,
                DIRECTIVE_SET,
                registers,
                memory,
                new PrintStreamLog(System.out)
        );

        assembler.assemble();

        var startAddress = assembler.getStartAddres();
        if (startAddress.isPresent()) {
            assembler.getRegisters().getProgramCounter().setValue(startAddress.getAsInt());
        } else {
            System.out.println("Global label 'main' not found. " +
                    "Execution will start at the start of the text section.");
        }

        var data = new MIPSSimulationData(
                CONFIG,
                new File(""),
                assembler.getLog(),
                new MIPSSimulationSource(assembler.getOriginals(), assembler.getAllLabels(), assembler.getGlobalScope()),
                assembler.getInstructionSet(),
                assembler.getRegisters(),
                assembler.getMemory(),
                assembler.getStackBottom(),
                assembler.getKernelStackBottom()
        );

        data.memory().saveState();
        data.registers().saveState();
        return assembler.createSimulation(architecture, data);
    }

}

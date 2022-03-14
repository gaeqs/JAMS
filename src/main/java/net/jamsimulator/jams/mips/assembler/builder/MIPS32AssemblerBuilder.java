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

package net.jamsimulator.jams.mips.assembler.builder;

import net.jamsimulator.jams.gui.util.log.Log;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.assembler.old.MIPS32Assembler;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.utils.RawFileData;

public final class MIPS32AssemblerBuilder extends AssemblerBuilder {

    public static final String NAME = "MIPS32";

    /**
     * Creates a MIPS32 builder.
     */
    MIPS32AssemblerBuilder(ResourceProvider provider) {
        super(provider, NAME);
    }

    @Override
    public MIPS32Assembler createAssembler(Iterable<RawFileData> rawFiles, DirectiveSet directiveSet, InstructionSet instructionSet,
                                           Registers registerSet, Memory memory, Log log) {
        return new MIPS32Assembler(rawFiles, instructionSet, directiveSet, registerSet, memory, log);
    }
}

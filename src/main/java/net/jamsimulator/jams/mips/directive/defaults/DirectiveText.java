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

package net.jamsimulator.jams.mips.directive.defaults;

import net.jamsimulator.jams.mips.assembler.MIPS32AssemblerLine;
import net.jamsimulator.jams.mips.assembler.SelectedMemorySegment;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.OptionalInt;

public class DirectiveText extends Directive {

    public static final String NAME = "text";
    private static final DirectiveParameterType[] PARAMETERS = {DirectiveParameterType.INT};


    public DirectiveText() {
        super(NAME, PARAMETERS, false, true);
    }

    @Override
    public OptionalInt onAddressAssignation(MIPS32AssemblerLine line, String[] parameters, String rawParameters) {
        int current = line.getAssembler().getAssemblerData().getCurrent();
        if (parameters.length == 1) {
            int address;
            try {
                address = NumericUtils.decodeInteger(parameters[0]);
            } catch (NumberFormatException ex) {
                throw new AssemblerException(line.getIndex(), "." + NAME + "'s first parameter must be a number!");
            }

            if (!line.getAssembler().getMemory().getMemorySectionName(address).equals(MIPS32Memory.TEXT_NAME)) {
                throw new AssemblerException(line.getIndex(), "Given address is not inside the text memory section");
            }

            line.getAssembler().getAssemblerData().setCurrentText(address);

        } else if (parameters.length != 0)
            throw new AssemblerException(line.getIndex(), "." + NAME + " directive must have one or zero parameters.");
        line.getAssembler().getAssemblerData().setSelected(SelectedMemorySegment.TEXT);
        return OptionalInt.of(current);
    }
}

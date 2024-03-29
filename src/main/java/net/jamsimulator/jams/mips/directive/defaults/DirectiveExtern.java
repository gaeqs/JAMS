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

import net.jamsimulator.jams.mips.assembler.MIPS32AssemblerData;
import net.jamsimulator.jams.mips.assembler.MIPS32AssemblerLine;
import net.jamsimulator.jams.mips.assembler.SelectedMemorySegment;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.OptionalInt;

public class DirectiveExtern extends Directive {

    public static final String NAME = "extern";
    private static final DirectiveParameterType[] PARAMETERS = {DirectiveParameterType.LABEL, DirectiveParameterType.POSITIVE_INT};

    public DirectiveExtern() {
        super(NAME, PARAMETERS, false, false, false);
    }

    @Override
    public OptionalInt onAddressAssignation(MIPS32AssemblerLine line, String[] parameters, String rawParameters) {
        if (parameters.length != 2)
            throw new AssemblerException(line.getIndex(), "." + NAME + " must have two parameter.");

        if (!NumericUtils.isInteger(parameters[1]))
            throw new AssemblerException(parameters[1] + " is not a number.");
        int i = NumericUtils.decodeInteger(parameters[1]);
        if (i < 0)
            throw new AssemblerException(i + " cannot be negative.");

        MIPS32AssemblerData data = line.getAssembler().getAssemblerData();
        SelectedMemorySegment old = data.getSelected();
        data.setSelected(SelectedMemorySegment.EXTERN);
        data.align(0);
        int start = data.getCurrent();
        data.addCurrent(i);

        var label = parameters[0];

        if (line.getScope() != line.getFile().getScope()) {
            throw new AssemblerException(line.getIndex(), "Cannot use ." + NAME + " on a macro scope!");
        }

        var global = line.getAssembler().getGlobalScope();
        global.addLabel(line.getIndex(), new Label(label, global, start, line.getFile().getName(), line.getIndex()));

        data.setSelected(old);
        return OptionalInt.of(start);
    }

}

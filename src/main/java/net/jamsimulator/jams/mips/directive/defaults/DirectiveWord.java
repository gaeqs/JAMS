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
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.mips.label.LabelReference;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.OptionalInt;

public class DirectiveWord extends Directive {

    public static final String NAME = "word";
    private static final DirectiveParameterType[] PARAMETERS = {DirectiveParameterType.INT_OR_LABEL};

    public DirectiveWord() {
        super(NAME, PARAMETERS, true, false, true);
    }

    @Override
    public OptionalInt onAddressAssignation(MIPS32AssemblerLine line, String[] parameters, String rawParameters) {
        if (parameters.length < 1)
            throw new AssemblerException(line.getIndex(), "." + NAME + " must have at least one parameter.");

        var data = line.getAssembler().getAssemblerData();
        data.align(2);
        int start = data.getCurrent();
        data.addCurrent(4 * parameters.length);
        return OptionalInt.of(start);
    }

    @Override
    public void onValueAssignation(MIPS32AssemblerLine line, String[] parameters, String rawParameters) {
        var memory = line.getAssembler().getMemory();
        var address = line.getAddress();
        for (String parameter : parameters) {
            var optional = NumericUtils.decodeIntegerSafe(parameter);
            if (optional.isEmpty()) {
                var label = line.getScope().resolveLabel(line.getAddress(), parameter);
                if (label.isEmpty()) {
                    throw new AssemblerException(line.getIndex(), "Label " + parameter + " not found.");
                }

                label.get().addReference(new LabelReference(address, line.getFile().getName(), line.getIndex()));
                memory.setWord(address, label.get().getAddress(), false, true, true);
            } else {
                memory.setWord(address, optional.get(), false, true, true);
            }
            address += 4;
        }
    }

}

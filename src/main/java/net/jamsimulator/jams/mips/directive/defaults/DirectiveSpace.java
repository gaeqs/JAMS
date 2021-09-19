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

import net.jamsimulator.jams.gui.mips.editor.indexing.MIPSEditorIndex;
import net.jamsimulator.jams.mips.assembler.MIPS32AssemblerData;
import net.jamsimulator.jams.mips.assembler.MIPS32AssemblingFile;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;
import net.jamsimulator.jams.utils.NumericUtils;

public class DirectiveSpace extends Directive {

    public static final String NAME = "space";
    private static final DirectiveParameterType[] PARAMETERS = {DirectiveParameterType.POSITIVE_INT};

    public DirectiveSpace() {
        super(NAME, PARAMETERS, false, false);
    }

    @Override
    public int execute(int lineNumber, String line, String[] parameters, String labelSufix, MIPS32AssemblingFile file) {
        if (parameters.length != 1)
            throw new AssemblerException(lineNumber, "." + NAME + " must have one parameter.");

        if (!NumericUtils.isInteger(parameters[0]))
            throw new AssemblerException(parameters[0] + " is not a number.");
        int i = NumericUtils.decodeInteger(parameters[0]);
        if (i < 0) throw new AssemblerException(i + " cannot be negative.");

        MIPS32AssemblerData data = file.getAssembler().getAssemblerData();
        data.align(0);
        int start = data.getCurrent();
        data.addCurrent(i);
        return start;
    }

    @Override
    public void postExecute(String[] parameters, MIPS32AssemblingFile file, int lineNumber, int address, String labelSufix) {

    }

    @Override
    public boolean isParameterValidInContext(int index, String value, int amount, MIPSEditorIndex context) {
        return isParameterValid(index, value);
    }
}

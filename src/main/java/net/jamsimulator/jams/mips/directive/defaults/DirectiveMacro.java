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

import net.jamsimulator.jams.mips.assembler.MIPS32AssemblingFile;
import net.jamsimulator.jams.mips.assembler.Macro;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;

import java.util.ArrayList;
import java.util.List;

public class DirectiveMacro extends Directive {

    public static final String NAME = "macro";
    private static final DirectiveParameterType[] PARAMETERS = {DirectiveParameterType.ANY, DirectiveParameterType.ANY};

    public DirectiveMacro() {
        super(NAME, PARAMETERS, true, false);
    }

    private static void panic(int line, String value, int i) {
        throw new AssemblerException(line, "Invalid macro parameter '" + value + "'! (Index " + i + ")");
    }

    @Override
    public int execute(int lineNumber, String line, String[] parameters, String labelSufix, MIPS32AssemblingFile file) {
        if (parameters.length < 1)
            throw new AssemblerException(lineNumber, "." + NAME + " must have at least one parameter.");

        String name = parameters[0];
        if (name.contains("(") || name.contains(")"))
            throw new AssemblerException(lineNumber, "Macro name cannot contain parenthesis!");
        if (file.getMacro(name).isPresent()) {
            throw new AssemblerException(lineNumber, "Macro " + name + " already exists!");
        }

        List<String> macroParameters = new ArrayList<>();

        for (int i = 1; i < parameters.length; i++) {
            var value = parameters[i];

            switch (value) {
                case "(" -> {
                    if (i == 1) continue;
                    panic(lineNumber, value, i);
                }
                case "()" -> {
                    if (i == 1 && parameters.length - 1 == 1) continue;
                    panic(lineNumber, value, i);
                }
                case ")" -> {
                    if (i == parameters.length - 1) continue;
                    panic(lineNumber, value, i);
                }
            }

            if (value.startsWith("(")) {
                if (i != 1) panic(lineNumber, value, i);
                value = value.substring(1);
            }

            if (value.endsWith(")")) {
                if (i != parameters.length - 1) panic(lineNumber, value, i);
                value = value.substring(0, value.length() - 1);
            }

            if (!value.startsWith("%")) panic(lineNumber, value, i);
            if (value.length() == 1) panic(lineNumber, value, 1);

            macroParameters.add(value);
        }

        file.startMacro(new Macro(name, macroParameters.toArray(new String[0])));

        return -1;
    }

    @Override
    public void postExecute(String[] parameters, MIPS32AssemblingFile file, int lineNumber, int address, String labelSufix) {

    }

}

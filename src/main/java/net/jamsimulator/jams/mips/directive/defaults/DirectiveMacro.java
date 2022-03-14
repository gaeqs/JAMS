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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public void onDiscovery(MIPS32AssemblerLine line, String[] parameters, String rawParameters, Map<String, String> equivalents) {
        if (parameters.length < 1)
            throw new AssemblerException(line.getIndex(), "." + NAME + " must have at least one parameter.");

        String name = parameters[0];
        if (name.contains("(") || name.contains(")"))
            throw new AssemblerException(line.getIndex(), "Macro name cannot contain parenthesis!");
        if (line.getFile().getLocalMacro(name).isPresent()) {
            throw new AssemblerException(line.getIndex(), "Macro " + name + " is already defined in the same file!");
        }
        if (line.getAssembler().getGlobalMacro(name).isPresent()) {
            throw new AssemblerException(line.getIndex(), "Macro " + name
                    + " is already defined in another file as a global macro!");
        }

        List<String> macroParameters = new ArrayList<>();

        for (int i = 1; i < parameters.length; i++) {
            var value = parameters[i];

            switch (value) {
                case "(" -> {
                    if (i == 1) continue;
                    panic(line.getIndex(), value, i);
                }
                case "()" -> {
                    if (i == 1 && parameters.length - 1 == 1) continue;
                    panic(line.getIndex(), value, i);
                }
                case ")" -> {
                    if (i == parameters.length - 1) continue;
                    panic(line.getIndex(), value, i);
                }
            }

            if (value.startsWith("(")) {
                if (i != 1) panic(line.getIndex(), value, i);
                value = value.substring(1);
            }

            if (value.endsWith(")")) {
                if (i != parameters.length - 1) panic(line.getIndex(), value, i);
                value = value.substring(0, value.length() - 1);
            }

            if (!value.startsWith("%")) panic(line.getIndex(), value, i);
            if (value.length() == 1) panic(line.getIndex(), value, 1);

            macroParameters.add(value);
        }

        line.getFile().startMacroDefinition(
                line.getIndex(),
                line.getFile().getName(),
                name,
                macroParameters.toArray(new String[0])
        );
    }

}

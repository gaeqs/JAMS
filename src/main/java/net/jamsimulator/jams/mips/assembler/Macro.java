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

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;

import java.util.ArrayList;
import java.util.List;

public class Macro {

    private final String name;
    private final String[] parameters;
    private final List<String> lines;

    public Macro(String name, String[] parameters) {
        this.name = name;
        this.parameters = parameters;
        this.lines = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public void executeMacro(String[] parameters, MIPS32AssemblingFile file, int lineNumber, int macroCall) {
        if (parameters.length != this.parameters.length)
            throw new AssemblerException("Macro " + name + " expected " + this.parameters.length +
                    " parameters but found " + parameters.length + ".");

        var sufix = "_M" + macroCall;

        for (String line : lines) {
            file.scanLine(lineNumber, parseLine(line, parameters), sufix);
        }
    }

    private String parseLine(String line, String[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            line = line.replace(this.parameters[i], parameters[i]);
        }

        return line;
    }
}

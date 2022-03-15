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
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a macro in an assembly code.
 */
public class Macro {

    private final String name;
    private final String[] parameters;
    private final List<String> lines;

    private final String originFile;
    private final int originLine;

    public Macro(String name, String[] parameters, String originFile, int originLine) {
        this.name = name;
        this.parameters = parameters;
        this.lines = new ArrayList<>();

        this.originFile = originFile;
        this.originLine = originLine;
    }

    public String getName() {
        return name;
    }

    public String getOriginFile() {
        return originFile;
    }

    public int getOriginLine() {
        return originLine;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public List<String> getLines() {
        return lines;
    }

    /**
     * Executes the macro in an external assembler. This allows creating macros in external assemblers easily.
     *
     * @param parameters the parameters to replace.
     * @param lineNumber the line executing this macro.
     * @param scanner    the consumer used to scan.
     */
    public void executeMacro(String[] parameters, int lineNumber, Consumer<String> scanner) {
        if (parameters.length != this.parameters.length)
            throw new AssemblerException(lineNumber, "Macro " + name + " expected " + this.parameters.length +
                    " parameters but found " + parameters.length + ".");

        for (String line : lines) {
            scanner.accept(parseLine(line, parameters));
        }
    }

    public List<String> getParsedLines(String[] parameters, int lineNumber) {
        if (parameters.length != this.parameters.length)
            throw new AssemblerException(lineNumber, "Macro " + name + " expected " + this.parameters.length +
                    " parameters but found " + parameters.length + ".");
        return lines.stream().map(it -> parseLine(it, parameters)).collect(Collectors.toList());
    }

    private String parseLine(String line, String[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            line = line.replace(this.parameters[i], parameters[i]);
        }

        return line;
    }
}

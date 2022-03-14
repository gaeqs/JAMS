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

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MIPS32AssemblerFile {

    private final MIPS32Assembler assembler;
    private final String name;

    private final List<MIPS32AssemblerLine> lines = new ArrayList<>();

    public MIPS32AssemblerFile(MIPS32Assembler assembler, String name, String rawData) {
        this.assembler = assembler;
        this.name = name;

        StringUtils.multiSplit(rawData, "\n", "\r")
                .forEach(line -> lines.add(new MIPS32AssemblerLine(this, line, lines.size())));
    }

    public MIPS32Assembler getAssembler() {
        return assembler;
    }

    public String getName() {
        return name;
    }

    public Optional<Label> getLocalLabel(String identifier) {
    }

    public Optional<Macro> getLocalMacro(String identifier) {
    }

}

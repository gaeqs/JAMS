package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;

import java.util.ArrayList;
import java.util.List;

public class MIPS32Macro {

    private final String name;
    private final String[] parameters;
    private final List<String> lines;

    public MIPS32Macro(String name, String[] parameters) {
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

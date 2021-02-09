package net.jamsimulator.jams.gui.mips.simulator.instruction;

import java.util.Optional;
import java.util.OptionalInt;

public class MIPSCompiledLine {

    private final int line;
    private final int address;
    private final int code;
    private final boolean hasCode;

    public MIPSCompiledLine(int line, int address, int code) {
        this.line = line;
        this.address = address;
        this.code = code;
        hasCode = true;
    }

    public MIPSCompiledLine(int line) {
        this.line = line;
        this.address = -1;
        this.code = -1;
        hasCode = false;
    }

    public int getLine() {
        return line;
    }

    public Optional<Integer> getAddress() {
        return hasCode ? Optional.of(address) : Optional.empty();
    }

    public Optional<Integer> getCode() {
        return hasCode ? Optional.of(code) : Optional.empty();
    }
}

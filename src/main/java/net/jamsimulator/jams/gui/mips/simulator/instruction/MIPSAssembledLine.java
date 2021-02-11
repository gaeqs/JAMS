package net.jamsimulator.jams.gui.mips.simulator.instruction;

import java.util.Optional;

/**
 * Represents a line inside a {@link MIPSAssembledCodeViewer}.
 * <p>
 * Developers shouldn't store instances of this class, as they will be deleted
 * when the user changes the style of the viewer.
 */
public class MIPSAssembledLine {

    private final int line;
    private final int address;
    private final int code;
    private final boolean hasCode;

    /**
     * Creates the line with an address and an instruction code.
     * <p>
     * This constructor should be used by instructions.
     *
     * @param line    the index of the line.
     * @param address the address.
     * @param code    the instruction code.
     */
    public MIPSAssembledLine(int line, int address, int code) {
        this.line = line;
        this.address = address;
        this.code = code;
        hasCode = true;
    }

    /**
     * Creates the line without address and code.
     * <p>
     * This constructor should be used by labels.
     *
     * @param line the index of the line.
     */
    public MIPSAssembledLine(int line) {
        this.line = line;
        this.address = -1;
        this.code = -1;
        hasCode = false;
    }

    /**
     * Returns the index of the line.
     *
     * @return the index.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the addres of the line, if present.
     *
     * @return the address if present.
     */
    public Optional<Integer> getAddress() {
        return hasCode ? Optional.of(address) : Optional.empty();
    }

    /**
     * Returns the instruction code of the line, if present.
     *
     * @return the code if present.
     */
    public Optional<Integer> getCode() {
        return hasCode ? Optional.of(code) : Optional.empty();
    }
}

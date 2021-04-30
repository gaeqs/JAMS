package net.jamsimulator.jams.gui.mips.simulator.instruction;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enumeration containing all the types of representation an instruction has.
 */
public enum MIPSAssembledInstructionViewerElement {

    DISASSEMBLED(30, "SIMULATION_INSTRUCTION_VIEWER_DISASSEMBLED"),
    HEX_CODE(10, "SIMULATION_INSTRUCTION_VIEWER_HEXADECIMAL"),
    ORIGINAL(50, "SIMULATION_INSTRUCTION_VIEWER_ORIGINAL");

    private final int maximumLength;
    private final String languageNode;

    MIPSAssembledInstructionViewerElement(int maximumLength, String languageNode) {
        this.maximumLength = maximumLength;
        this.languageNode = languageNode;
    }

    /**
     * Returns the maximum text length of this representation.
     *
     * @return the maximum length.
     */
    public int getMaximumLength() {
        return maximumLength;
    }

    /**
     * Returns the language node of this representation.
     *
     * @return the language node.
     */
    public String getLanguageNode() {
        return languageNode;
    }

    /**
     * Returns the element that matches the given name, if present.
     *
     * @param name the name.
     * @return the elemennt if present.
     */
    public static Optional<MIPSAssembledInstructionViewerElement> getByName(String name) {
        return Arrays.stream(values()).filter(target -> target.name().equals(name)).findAny();
    }
}

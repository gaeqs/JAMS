package net.jamsimulator.jams.gui.mips.simulator.instruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class manages the order different {@link MIPSAssembledInstructionViewerElement element}s are shown
 * in the instruction viewer.
 */
public class MIPSAssembledInstructionViewerOrder {

    public static final MIPSAssembledInstructionViewerOrder DEFAULT = new MIPSAssembledInstructionViewerOrder();

    static {
        DEFAULT.addElement(MIPSAssembledInstructionViewerElement.HEX_CODE);
        DEFAULT.addElement(MIPSAssembledInstructionViewerElement.DISASSEMBLED);
        DEFAULT.addElement(MIPSAssembledInstructionViewerElement.ORIGINAL);
    }

    private final List<MIPSAssembledInstructionViewerElement> elements;

    /**
     * Creates the instance of the manager.
     */
    public MIPSAssembledInstructionViewerOrder() {
        elements = new ArrayList<>(3);
    }

    /**
     * Returns an unmodifiable list containing the elements of this manager.
     *
     * @return the list.
     */
    public List<MIPSAssembledInstructionViewerElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * Returns the amount of elements this manager has.
     *
     * @return the amount of elements.o
     */
    public int getSize() {
        return elements.size();
    }

    /**
     * Adds an element at the given index.
     *
     * @param index   the index.
     * @param element the element.
     */
    public void addElement(int index, MIPSAssembledInstructionViewerElement element) {
        elements.add(index, element);
    }

    /**
     * Adds an element at the end of this manager.
     *
     * @param element the element.
     */
    public void addElement(MIPSAssembledInstructionViewerElement element) {
        elements.add(element);
    }

    /**
     * Removes the element at the given index. from this manager.
     *
     * @param index the index.
     */
    public void removeElement(int index) {
        elements.remove(index);
    }

}

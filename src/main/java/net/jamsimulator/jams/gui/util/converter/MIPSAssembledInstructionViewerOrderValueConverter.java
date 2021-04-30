package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledInstructionViewerElement;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledInstructionViewerOrder;

import java.util.Optional;

public class MIPSAssembledInstructionViewerOrderValueConverter extends ValueConverter<MIPSAssembledInstructionViewerOrder> {

    public static final String NAME = "assembled_instruction_viewer_order";

    @Override
    public String toString(MIPSAssembledInstructionViewerOrder value) {
        if (value == null) return null;
        var elements = value.getElements();
        var builder = new StringBuilder();
        for (MIPSAssembledInstructionViewerElement element : elements) {
            builder.append(element.name()).append(";");
        }

        return builder.length() == 0 ? "" : builder.substring(0, builder.length() - 1);
    }

    @Override
    public Optional<MIPSAssembledInstructionViewerOrder> fromStringSafe(String value) {
        var elements = new MIPSAssembledInstructionViewerOrder();
        var split = value.split(";");

        for (String element : split) {
            var optional =
                    MIPSAssembledInstructionViewerElement.getByName(element);
            optional.ifPresent(elements::addElement);
        }

        return Optional.of(elements);
    }

    @Override
    public Class<?> conversionClass() {
        return Action.class;
    }
}

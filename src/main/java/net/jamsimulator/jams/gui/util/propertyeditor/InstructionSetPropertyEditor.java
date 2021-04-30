package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;

import java.util.function.Consumer;

public class InstructionSetPropertyEditor extends ComboBox<InstructionSet> implements PropertyEditor<InstructionSet> {

    private final Property<InstructionSet> property;
    private Consumer<InstructionSet> listener = p -> {
    };


    public InstructionSetPropertyEditor(Property<InstructionSet> property) {
        this.property = property;

        setConverter(ValueConverters.getByTypeUnsafe(InstructionSet.class));
        getItems().addAll(Jams.getInstructionSetManager());
        getSelectionModel().select(property.getValue());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            property.setValue(val);
            listener.accept(val);
        });

    }

    @Override
    public Property<InstructionSet> getProperty() {
        return property;
    }

    @Override
    public Node thisInstanceAsNode() {
        return this;
    }

    @Override
    public void addListener(Consumer<InstructionSet> consumer) {
        listener = listener.andThen(consumer);
    }
}

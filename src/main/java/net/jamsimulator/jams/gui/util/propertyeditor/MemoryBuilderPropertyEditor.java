package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.memory.builder.MemoryBuilder;

import java.util.function.Consumer;

public class MemoryBuilderPropertyEditor extends ComboBox<MemoryBuilder> implements PropertyEditor<MemoryBuilder> {

    private final Property<MemoryBuilder> property;
    private Consumer<MemoryBuilder> listener = p -> {
    };


    public MemoryBuilderPropertyEditor(Property<MemoryBuilder> property) {
        this.property = property;

        setConverter(ValueConverters.getByTypeUnsafe(MemoryBuilder.class));
        getItems().addAll(Jams.getMemoryBuilderManager());
        getSelectionModel().select(property.getValue());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            property.setValue(val);
            listener.accept(val);
        });

    }

    @Override
    public Property<MemoryBuilder> getProperty() {
        return property;
    }

    @Override
    public Node thisInstanceAsNode() {
        return this;
    }

    @Override
    public void addListener(Consumer<MemoryBuilder> consumer) {
        listener = listener.andThen(consumer);
    }
}

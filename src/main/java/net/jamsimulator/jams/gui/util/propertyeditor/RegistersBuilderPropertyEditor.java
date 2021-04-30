package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.function.Consumer;

public class RegistersBuilderPropertyEditor extends ComboBox<RegistersBuilder> implements PropertyEditor<RegistersBuilder> {

    private final Property<RegistersBuilder> property;
    private Consumer<RegistersBuilder> listener = p -> {
    };


    public RegistersBuilderPropertyEditor(Property<RegistersBuilder> property) {
        this.property = property;

        setConverter(ValueConverters.getByTypeUnsafe(RegistersBuilder.class));
        getItems().addAll(Jams.getRegistersBuilderManager());
        getSelectionModel().select(property.getValue());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            property.setValue(val);
            listener.accept(val);
        });

    }

    @Override
    public Property<RegistersBuilder> getProperty() {
        return property;
    }

    @Override
    public Node thisInstanceAsNode() {
        return this;
    }

    @Override
    public void addListener(Consumer<RegistersBuilder> consumer) {
        listener = listener.andThen(consumer);
    }
}

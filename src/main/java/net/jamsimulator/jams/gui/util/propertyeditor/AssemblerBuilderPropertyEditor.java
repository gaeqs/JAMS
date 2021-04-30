package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilder;

import java.util.function.Consumer;

public class AssemblerBuilderPropertyEditor extends ComboBox<AssemblerBuilder> implements PropertyEditor<AssemblerBuilder> {

    private final Property<AssemblerBuilder> property;
    private Consumer<AssemblerBuilder> listener = p -> {
    };


    public AssemblerBuilderPropertyEditor(Property<AssemblerBuilder> property) {
        this.property = property;

        setConverter(ValueConverters.getByTypeUnsafe(AssemblerBuilder.class));
        getItems().addAll(Jams.getAssemblerBuilderManager());
        getSelectionModel().select(property.getValue());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            property.setValue(val);
            listener.accept(val);
        });

    }

    @Override
    public Property<AssemblerBuilder> getProperty() {
        return property;
    }

    @Override
    public Node thisInstanceAsNode() {
        return this;
    }

    @Override
    public void addListener(Consumer<AssemblerBuilder> consumer) {
        listener = listener.andThen(consumer);
    }
}

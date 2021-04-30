package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;

import java.util.function.Consumer;

public class DirectiveSetPropertyEditor extends ComboBox<DirectiveSet> implements PropertyEditor<DirectiveSet> {

    private final Property<DirectiveSet> property;
    private Consumer<DirectiveSet> listener = p -> {
    };


    public DirectiveSetPropertyEditor(Property<DirectiveSet> property) {
        this.property = property;

        setConverter(ValueConverters.getByTypeUnsafe(DirectiveSet.class));
        getItems().addAll(Jams.getDirectiveSetManager());
        getSelectionModel().select(property.getValue());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            property.setValue(val);
            listener.accept(val);
        });

    }

    @Override
    public Property<DirectiveSet> getProperty() {
        return property;
    }

    @Override
    public Node thisInstanceAsNode() {
        return this;
    }

    @Override
    public void addListener(Consumer<DirectiveSet> consumer) {
        listener = listener.andThen(consumer);
    }
}

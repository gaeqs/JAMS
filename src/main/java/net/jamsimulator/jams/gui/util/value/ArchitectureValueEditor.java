package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.converter.ActionValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.architecture.event.ArchitectureRegisterEvent;
import net.jamsimulator.jams.mips.architecture.event.ArchitectureUnregisterEvent;

import java.util.function.Consumer;

public class ArchitectureValueEditor extends ComboBox<Architecture> implements ValueEditor<Architecture> {

    public static final String NAME = ActionValueConverter.NAME;

    private Consumer<Architecture> listener = architecture -> {
    };

    public ArchitectureValueEditor() {
        setConverter(ValueConverters.getByTypeUnsafe(Architecture.class));
        getItems().addAll(Jams.getArchitectureManager());
        getSelectionModel().select(Jams.getArchitectureManager().getDefault());
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        Jams.getArchitectureManager().registerListeners(this, true);
    }

    @Override
    public void setCurrentValue(Architecture value) {
        getSelectionModel().select(value);
    }

    @Override
    public Architecture getCurrentValue() {
        return getValue();
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        var box = new HBox(label, this);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @Override
    public void addListener(Consumer<Architecture> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<Architecture> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(Architecture.class);
    }

    @Listener
    private void onArchitectureRegister(ArchitectureRegisterEvent.After event) {
        getItems().add(event.getArchitecture());
    }

    @Listener
    private void onArchitectureUnregister(ArchitectureUnregisterEvent.After event) {
        if (getSelectionModel().getSelectedItem().equals(event.getArchitecture()))
            setValue(Jams.getArchitectureManager().getDefault());
        getItems().remove(event.getArchitecture());
    }

    public static class Builder implements ValueEditor.Builder<Architecture> {

        @Override
        public ValueEditor<Architecture> build() {
            return new ArchitectureValueEditor();
        }

    }
}

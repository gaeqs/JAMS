package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class StringValueEditor extends TextField implements ValueEditor<String> {

    public static final String NAME = "string";

    private Consumer<String> listener = string -> {
    };

    public StringValueEditor() {
        setOnAction(target -> listener.accept(getText()));
        focusedProperty().addListener((obs, old, val) -> {
            if (!val) listener.accept(getText());
        });
    }

    @Override
    public void setCurrentValue(String value) {
        setText(value);
        listener.accept(value);
    }

    @Override
    public String getCurrentValue() {
        return getText();
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
    public void addListener(Consumer<String> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<String> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(String.class);
    }

    public static class Builder implements ValueEditor.Builder<String> {

        @Override
        public ValueEditor<String> build() {
            return new StringValueEditor();
        }

    }
}

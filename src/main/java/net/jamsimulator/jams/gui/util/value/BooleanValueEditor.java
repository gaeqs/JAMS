package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.BooleanValueConverter;

import java.util.function.Consumer;

public class BooleanValueEditor extends CheckBox implements ValueEditor<Boolean> {

    public static final String NAME = BooleanValueConverter.NAME;

    private Consumer<Boolean> listener = b -> {
    };

    public BooleanValueEditor() {
        selectedProperty().addListener((obs, old, val) -> listener.accept(val));
    }

    @Override
    public void setCurrentValue(Boolean value) {
        setSelected(value);
    }

    @Override
    public Boolean getCurrentValue() {
        return isSelected();
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        label.setOnMouseClicked(click -> setCurrentValueUnsafe(!((boolean) getCurrentValue())));
        var box = new HBox(this, label);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @Override
    public void addListener(Consumer<Boolean> consumer) {
        listener = listener.andThen(consumer);
    }

    public static class Builder implements ValueEditor.Builder<Boolean> {

        @Override
        public ValueEditor<Boolean> build() {
            return new BooleanValueEditor();
        }

    }
}

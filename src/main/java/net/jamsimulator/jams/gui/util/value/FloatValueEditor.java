package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.FloatValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class FloatValueEditor extends TextField implements ValueEditor<Float> {

    public static final String NAME = FloatValueConverter.NAME;

    protected String oldText;

    private Consumer<Float> listener = f -> {
    };

    public FloatValueEditor() {
        setText("0.0");
        oldText = getText();

        Runnable run = () -> {
            if (oldText.equals(getText())) return;
            try {
                float number = Float.parseFloat(getText());

                listener.accept(number);

                oldText = getText();
            } catch (NumberFormatException ex) {
                setText(oldText);
            }
        };

        setOnAction(event -> run.run());
        focusedProperty().addListener((obs, old, val) -> {
            if (val) return;
            run.run();
        });
    }

    @Override
    public void setCurrentValue(Float value) {
        setText(String.valueOf(value));
        listener.accept(value);
    }

    @Override
    public Float getCurrentValue() {
        return Float.valueOf(getText());
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
    public void addListener(Consumer<Float> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<Float> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(Float.class);
    }


    public static class Builder implements ValueEditor.Builder<Float> {

        @Override
        public ValueEditor<Float> build() {
            return new FloatValueEditor();
        }

    }
}

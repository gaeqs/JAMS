package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.function.Consumer;

public class RangedIntegerValueEditor extends TextField implements ValueEditor<Integer> {

    public static final String NAME = "positive_integer";

    protected String oldText;
    protected int min, max;

    private Consumer<Integer> listener = i -> {
    };

    public RangedIntegerValueEditor() {
        setText("0");
        oldText = getText();
        min = Integer.MIN_VALUE;
        max = Integer.MAX_VALUE;

        Runnable run = () -> {
            if (oldText.equals(getText())) return;
            try {
                int number = NumericUtils.decodeInteger(getText());
                if (number < min || number > max) {
                    setText(oldText);
                    return;
                }

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

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public void setMin(int min) {
        this.min = min;
        setCurrentValue(getCurrentValue());
    }

    public void setMax(int max) {
        this.max = max;
        setCurrentValue(getCurrentValue());
    }

    @Override
    public void setCurrentValue(Integer value) {
        int positive = Math.max(min, Math.min(max, value));
        setText(oldText = String.valueOf(positive));
        listener.accept(positive);
    }

    @Override
    public Integer getCurrentValue() {
        return Integer.valueOf(getText());
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
    public void addListener(Consumer<Integer> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<Integer> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(Integer.class);
    }

    public static class Builder implements ValueEditor.Builder<Integer> {

        @Override
        public ValueEditor<Integer> build() {
            return new RangedIntegerValueEditor();
        }

    }
}

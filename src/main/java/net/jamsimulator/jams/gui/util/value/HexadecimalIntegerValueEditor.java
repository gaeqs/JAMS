package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.function.Consumer;

public class HexadecimalIntegerValueEditor extends TextField implements ValueEditor<Integer> {

    public static final String NAME = "hexadecimal_integer";

    protected String oldText;

    private Consumer<Integer> listener = architecture -> {
    };

    public HexadecimalIntegerValueEditor() {
        setText("0x00000000");
        oldText = getText();

        Runnable run = () -> {
            if (oldText.equals(getText())) return;
            try {
                int number = NumericUtils.decodeInteger(getText());

                listener.accept(number);
                setCurrentValue(number);
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
    public void setCurrentValue(Integer value) {
        setText("0x" + StringUtils.addZeros(Integer.toHexString(value), 8));
        listener.accept(value);
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
            return new HexadecimalIntegerValueEditor();
        }

    }
}

package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;

import java.util.function.Consumer;

public class Pow2ValueEditor extends ComboBox<String> implements ValueEditor<Integer> {

    public static final String NAME = "pow2";

    private static final char[] EXPONENTS = new char[]{'\u2070', '\u00B9', '\u00B2', '\u00B3',
            '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079'};

    private Consumer<Integer> listener = i -> {
    };

    public Pow2ValueEditor() {
        StringBuilder builder;
        for (int i = 0; i < 32; i++) {
            builder = new StringBuilder();
            var j = i;
            while (j > 9) {
                builder.append(EXPONENTS[j % 10]);
                j /= 10;
            }
            builder.append(EXPONENTS[j % 10]);

            getItems().add("2" + builder.reverse() + " (" + Integer.toUnsignedString(1 << i) + ")");
        }

        getSelectionModel().select(0);

        getSelectionModel().selectedIndexProperty().addListener((obs, old, val) -> listener.accept(val.intValue()));
    }

    @Override
    public void setCurrentValue(Integer value) {
        getSelectionModel().select(value);
    }

    @Override
    public Integer getCurrentValue() {
        return getSelectionModel().getSelectedIndex();
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
            return new Pow2ValueEditor();
        }

    }
}

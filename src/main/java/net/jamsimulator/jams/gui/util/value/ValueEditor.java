package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public interface ValueEditor<E> {

    void setCurrentValue(E value);

    default void setCurrentValueUnsafe(Object value) {
        setCurrentValue((E) value);
    }

    E getCurrentValue();

    Node getAsNode();

    Node buildConfigNode(Label label);

    void addListener(Consumer<E> consumer);

    interface Builder<E> {

        ValueEditor<E> build();

    }

}

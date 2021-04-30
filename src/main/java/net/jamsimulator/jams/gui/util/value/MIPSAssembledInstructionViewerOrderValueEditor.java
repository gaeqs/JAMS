package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledInstructionViewerElement;
import net.jamsimulator.jams.gui.mips.simulator.instruction.MIPSAssembledInstructionViewerOrder;
import net.jamsimulator.jams.gui.util.converter.MIPSAssembledInstructionViewerOrderValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

import java.util.function.Consumer;

public class MIPSAssembledInstructionViewerOrderValueEditor extends VBox implements ValueEditor<MIPSAssembledInstructionViewerOrder> {

    public static final String NAME = MIPSAssembledInstructionViewerOrderValueConverter.NAME;

    private final HBox topBox, usingBox;

    private Consumer<MIPSAssembledInstructionViewerOrder> listener = string -> {
    };

    public MIPSAssembledInstructionViewerOrderValueEditor() {
        setFillWidth(true);
        setAlignment(Pos.BOTTOM_CENTER);
        setPadding(new Insets(0, 0, 0, 10));

        topBox = new HBox();
        usingBox = new HBox();

        topBox.setPrefHeight(30);
        usingBox.setPrefHeight(30);
        topBox.setSpacing(5);
        usingBox.setSpacing(5);

        var usingLabel = new LanguageLabel(Messages.CONFIG_SIMULATION_MIPS_INSTRUCTIONS_VIEWER_ELEMENTS_ORDER_USING);
        var notUsingLabel = new LanguageLabel(Messages.CONFIG_SIMULATION_MIPS_INSTRUCTIONS_VIEWER_ELEMENTS_ORDER_NOT_USING);

        var usingHBox = new HBox(usingLabel, usingBox);
        var notUsingHBox = new HBox(notUsingLabel, topBox);

        usingHBox.setAlignment(Pos.CENTER_LEFT);
        notUsingHBox.setAlignment(Pos.CENTER_LEFT);
        usingHBox.setSpacing(10);
        notUsingHBox.setSpacing(10);

        getChildren().addAll(notUsingHBox, usingHBox);
    }

    @Override
    public void setCurrentValue(MIPSAssembledInstructionViewerOrder value) {
        var present = value.getElements();

        for (var element : present) {
            usingBox.getChildren().add(new ElementNode(element, true));
        }

        for (var element : MIPSAssembledInstructionViewerElement.values()) {
            if (!present.contains(element)) {
                topBox.getChildren().add(new ElementNode(element, false));
            }
        }

        listener.accept(value);
    }

    @Override
    public MIPSAssembledInstructionViewerOrder getCurrentValue() {
        var elements = new MIPSAssembledInstructionViewerOrder();
        for (Node child : usingBox.getChildren()) {
            var node = (ElementNode) child;
            elements.addElement(node.element);
        }

        return elements;
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        var box = new VBox(label, this);
        box.setSpacing(5);
        box.setFillWidth(true);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @Override
    public void addListener(Consumer<MIPSAssembledInstructionViewerOrder> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<MIPSAssembledInstructionViewerOrder> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(MIPSAssembledInstructionViewerOrder.class);
    }

    public static class Builder implements ValueEditor.Builder<MIPSAssembledInstructionViewerOrder> {

        @Override
        public ValueEditor<MIPSAssembledInstructionViewerOrder> build() {
            return new MIPSAssembledInstructionViewerOrderValueEditor();
        }

    }

    public class ElementNode extends LanguageButton {

        private final MIPSAssembledInstructionViewerElement element;
        private boolean using;

        public ElementNode(MIPSAssembledInstructionViewerElement element, boolean using) {
            super(element.getLanguageNode());
            this.element = element;
            this.using = using;
            initAction();
        }

        private void initAction() {
            setOnAction(event -> {
                if (using) {
                    usingBox.getChildren().remove(this);
                    topBox.getChildren().add(this);
                } else {
                    topBox.getChildren().remove(this);
                    usingBox.getChildren().add(this);
                }
                using = !using;

                listener.accept(getCurrentValue());
            });
        }
    }
}

/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.util.DraggableListCell;
import net.jamsimulator.jams.gui.util.converter.ALUCollectionSnapshotValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.instruction.alu.ALU;
import net.jamsimulator.jams.mips.instruction.alu.ALUCollectionSnapshot;
import net.jamsimulator.jams.mips.instruction.alu.ALUType;

import java.util.function.Consumer;

public class ALUCollectionSnapshotValueEditor extends ListView<ALU> implements ValueEditor<ALUCollectionSnapshot> {

    public static final String NAME = ALUCollectionSnapshotValueConverter.NAME;
    public static final String STYLE_CLASS = GENERAL_STYLE_CLASS + "-" + NAME.replace("_", "-");
    private static final float ICON_SIZE = 20.0f;

    private ALUCollectionSnapshot current = new ALUCollectionSnapshot();

    private Consumer<ALUCollectionSnapshot> listener = snapshot -> {
    };

    public ALUCollectionSnapshotValueEditor() {
        getStyleClass().addAll(GENERAL_STYLE_CLASS, STYLE_CLASS);

        setCellFactory(it -> new Cell());

        // Dummy element. Represents the addition button.
        getItems().add(new ALU(ALUType.INTEGER, 1));
    }

    @Override
    public ALUCollectionSnapshot getCurrentValue() {
        return current;
    }

    @Override
    public void setCurrentValue(ALUCollectionSnapshot value) {
        current = value;
        getItems().setAll(value);

        // Dummy element. Represents the addition button.
        getItems().add(new ALU(ALUType.INTEGER, 1));

        listener.accept(current);
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        var box = new HBox(label, this);
        box.getStyleClass().add(GENERAL_STYLE_CLASS + "-hbox");
        box.getStyleClass().add(STYLE_CLASS + "-hbox");
        box.setSpacing(5.0f);
        box.setAlignment(Pos.CENTER_LEFT);
        prefWidthProperty().bind(box.widthProperty().subtract(label.widthProperty()).subtract(30));
        return box;
    }

    @Override
    public void addListener(Consumer<ALUCollectionSnapshot> consumer) {
        listener = listener.andThen(consumer);
    }

    @Override
    public ValueConverter<ALUCollectionSnapshot> getLinkedConverter() {
        return ValueConverters.getByTypeUnsafe(ALUCollectionSnapshot.class);
    }

    private void refreshValues() {
        current = new ALUCollectionSnapshot(getItems().subList(0, getItems().size() - 1));
        listener.accept(current);
    }

    public static class Builder implements ValueEditor.Builder<ALUCollectionSnapshot> {

        @Override
        public ValueEditor<ALUCollectionSnapshot> build() {
            return new ALUCollectionSnapshotValueEditor();
        }

    }

    private class Cell extends DraggableListCell<ALU> {

        @Override
        protected void updateItem(ALU item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) setGraphic(null);
            else if (getItems().size() - 1 == getIndex()) {
                setDraggable(false);
                setGraphic(createAddButton());
            } else {
                setDraggable(true);
                var editor = new ALUValueEditor();
                editor.getChildren().add(0, new QualityImageView(Icons.CONTROL_DRAG, ICON_SIZE, ICON_SIZE));
                editor.getChildren().add(createRemoveButton());
                editor.setCurrentValueUnsafe(item);
                editor.addListener(it -> {
                    getItems().set(getIndex(), it);
                    refreshValues();
                });
                setGraphic(editor);
            }
        }

        @Override
        protected void onDragDropped(DragEvent event) {
            if (getItem() == null) return;
            var dragboard = event.getDragboard();

            if (dragboard.hasString()) {
                var items = getListView().getItems();

                int index = Integer.parseInt(dragboard.getString());
                if (index < 0 || index >= items.size()) return;
                int to = getIndex();
                items.add(to, items.remove(index));
                refreshValues();
            }
            event.consume();
        }

        private Button createRemoveButton() {
            var button = new Button(null, new QualityImageView(Icons.CONTROL_REMOVE, ICON_SIZE, ICON_SIZE));
            button.getStyleClass().add("button-bold");
            button.setCursor(Cursor.HAND);
            button.setOnAction(it -> {
                getItems().remove(getIndex());
                refreshValues();
            });
            return button;
        }

        private Button createAddButton() {
            var button = new Button(null, new QualityImageView(Icons.CONTROL_ADD, ICON_SIZE, ICON_SIZE));
            button.getStyleClass().add("button-bold");
            button.setCursor(Cursor.HAND);
            button.setOnAction(it -> {
                getItems().add(getItems().size() - 1, new ALU(ALUType.INTEGER, 1));
                refreshValues();
            });
            return button;
        }
    }
}

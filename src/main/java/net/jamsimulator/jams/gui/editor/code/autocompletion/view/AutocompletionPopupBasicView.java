/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.gui.editor.code.autocompletion.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionElementselectEvent;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionOption;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionPopup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutocompletionPopupBasicView extends ListView<AutocompletionOption<?>> implements AutocompletionPopupView {

    private final List<Integer> maxLengths = new ArrayList<>();

    private int maxKeyLength;
    private double zoom;

    private ObservableNumberValue cellsWidthProperty = new SimpleDoubleProperty(200);

    public AutocompletionPopupBasicView() {
        getStyleClass().add("autocompletion-popup");
        setCellFactory(it -> {
            var cell = new AutocompletionPopupBasicViewElement(this);
            cellsWidthProperty = Bindings.max(cellsWidthProperty,
                    Bindings.when(cell.emptyProperty())
                            .then(0)
                            .otherwise(cell.getContainer().widthProperty().add(20)));
            prefWidthProperty().bind(cellsWidthProperty);
            return cell;
        });
    }

    public List<Integer> getMaxLengths() {
        return maxLengths;
    }

    public int getMaxKeyLength() {
        return maxKeyLength;
    }

    public double getZoom() {
        return zoom;
    }

    @Override
    public void showContents(AutocompletionPopup popup, List<AutocompletionOption<?>> options) {
        getItems().clear();

        zoom = popup.getEditor().getZoom().getZoom().getY();

        maxLengths.clear();
        boolean next = true;
        int index = 0;
        while (next) {
            next = false;
            int max = 0;
            for (var option : options) {
                if (option.candidate().displayStrings().size() > index) {
                    max = Math.max(max, option.candidate().displayStrings().get(index).length());
                    next = true;
                }
            }
            if (next) {
                maxLengths.add(max);
                index++;
            }
        }

        if (!maxLengths.isEmpty()) maxLengths.set(maxLengths.size() - 1, 0);

        maxKeyLength = options.stream().mapToInt(it -> it.candidate().key().length())
                .max()
                .orElse(0);

        getItems().clear();
        getItems().addAll(options);

        setMaxHeight(200 * zoom);
        //setMaxWidth(500 * zoom);

        getSelectionModel().select(0);

        if (!getItems().isEmpty()) {
            popup.callEvent(new AutocompletionElementselectEvent(popup, getItems().get(0).candidate().element()));
        }
    }

    @Override
    public Node asNode() {
        return this;
    }

    @Override
    public Optional<String> getSelected() {
        return Optional.ofNullable(getSelectionModel().getSelectedItem()).map(it -> it.candidate().replacement());
    }

    @Override
    public Optional<Object> getSelectedElement() {
        return Optional.ofNullable(getSelectionModel().getSelectedItem()).map(it -> it.candidate().element());
    }

    @Override
    public void moveUp(AutocompletionPopup popup) {
        if (getItems().isEmpty()) return;

        int selectedElement = getSelectionModel().getSelectedIndex() - 1;
        if (selectedElement < 0) {
            selectedElement = getItems().size() - 1;
        }

        getSelectionModel().select(selectedElement);
        scrollTo(selectedElement);
        popup.callEvent(new AutocompletionElementselectEvent(popup, getItems().get(selectedElement).candidate().element()));
    }

    @Override
    public void moveDown(AutocompletionPopup popup) {
        if (getItems().isEmpty()) return;

        int selectedElement = getSelectionModel().getSelectedIndex() + 1;
        if (selectedElement >= getItems().size()) {
            selectedElement = 0;
        }

        getSelectionModel().select(selectedElement);
        scrollTo(selectedElement);
        popup.callEvent(new AutocompletionElementselectEvent(popup, getItems().get(selectedElement).candidate().element()));
    }
}

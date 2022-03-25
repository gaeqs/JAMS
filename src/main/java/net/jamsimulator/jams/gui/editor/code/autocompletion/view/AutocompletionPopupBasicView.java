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

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionOption;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionPopup;
import net.jamsimulator.jams.gui.util.PixelScrollPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutocompletionPopupBasicView extends PixelScrollPane implements AutocompletionPopupView {

    protected final VBox content = new VBox();
    private final List<AutocompletionPopupBasicViewElement> elements = new ArrayList<>();

    private int selectedElement;

    public AutocompletionPopupBasicView() {
        content.getStyleClass().add("autocompletion-popup");
        setContent(content);
        setFitToHeight(true);
        setFitToWidth(true);
    }

    @Override
    public void showContents(AutocompletionPopup popup, List<AutocompletionOption<?>> options) {
        elements.clear();

        double zoom = popup.getEditor().getZoom().getZoom().getY();

        var lengths = new ArrayList<Integer>();

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
                lengths.add(max);
                index++;
            }
        }

        lengths.set(lengths.size() - 1, 0);

        int maxKey = options.stream().mapToInt(it -> it.candidate().key().length())
                .max()
                .orElse(0);

        options.forEach(it -> elements.add(new AutocompletionPopupBasicViewElement(it, maxKey, lengths, zoom)));
        content.getChildren().clear();
        content.getChildren().addAll(elements);

        setMaxHeight(200 * zoom);
        setMinWidth(300 * zoom);

        selectedElement = 0;
        if(!elements.isEmpty()) {
            elements.get(0).setSelected(true);
        }
    }

    @Override
    public Node asNode() {
        return this;
    }

    @Override
    public Optional<String> getSelected() {
        return elements.isEmpty()
                ? Optional.empty()
                : Optional.of(elements.get(selectedElement).getOption().candidate().key());
    }

    @Override
    public void moveUp() {
        if (elements.isEmpty()) return;
        elements.get(selectedElement).setSelected(false);

        selectedElement--;
        if (selectedElement < 0) {
            selectedElement = elements.size() - 1;
        }

        elements.get(selectedElement).setSelected(true);
        ensureVisible(elements.get(selectedElement));
    }

    @Override
    public void moveDown() {
        if (elements.isEmpty()) return;
        elements.get(selectedElement).setSelected(false);

        selectedElement++;
        if (selectedElement >= elements.size()) {
            selectedElement = 0;
        }

        elements.get(selectedElement).setSelected(true);
        ensureVisible(elements.get(selectedElement));
    }
}

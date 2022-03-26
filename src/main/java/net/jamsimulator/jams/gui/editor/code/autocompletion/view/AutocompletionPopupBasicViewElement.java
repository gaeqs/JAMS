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

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionOption;
import net.jamsimulator.jams.gui.image.nearest.NearestImageView;
import net.jamsimulator.jams.utils.StringSearch;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.LinkedList;
import java.util.Optional;

public class AutocompletionPopupBasicViewElement extends ListCell<AutocompletionOption<?>> {

    public static final String STYLE_CLASS = "autocompletion-popup-element";
    public static final String CONTAINER_STYLE_CLASS = "autocompletion-popup-element-container";
    public static final String SELECTED_STYLE_CLASS = "autocompletion-popup-element-selected";
    public static final String SLICE_STYLE_CLASS = "autocompletion-popup-element-key-slice";
    public static final String SLICE_MATCH_STYLE_CLASS = "autocompletion-popup-element-key-slice-match";

    private static final double DEFAULT_FONT_SIZE = 13.5;

    private final AutocompletionPopupBasicView view;

    private final HBox container = new HBox();
    private final HBox keyRegion = new HBox();
    private final LinkedList<Label> displayLabels = new LinkedList<>();

    private AutocompletionOption<?> last;

    public AutocompletionPopupBasicViewElement(AutocompletionPopupBasicView view) {
        getStyleClass().add(STYLE_CLASS);
        container.getStyleClass().add(CONTAINER_STYLE_CLASS);

        this.view = view;
    }

    public HBox getContainer() {
        return container;
    }

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
        if (isSelected()) {
            getStyleClass().add(SELECTED_STYLE_CLASS);
        } else {
            getStyleClass().removeAll(SELECTED_STYLE_CLASS);
        }
    }

    @Override
    protected void updateItem(AutocompletionOption<?> option, boolean empty) {
        super.updateItem(option, empty);
        if (option != null && !empty) {
            setGraphic(new Group(container));
            if (last == option) return;

            container.getChildren().clear();
            displayLabels.clear();

            double size = DEFAULT_FONT_SIZE * view.getZoom();
            var labelStyle = "-fx-font-size: " + (size) + ";";

            int index = 0;
            for (var string : option.candidate().displayStrings()) {
                var label = new Label(StringUtils.addSpaces(string, view.getMaxLengths().get(index++), true));
                label.setStyle(labelStyle);
                displayLabels.add(label);
            }

            populateKeyHbox(
                    StringUtils.addSpaces(option.candidate().key(), view.getMaxKeyLength(), true),
                    option.searchResult(),
                    labelStyle
            );

            loadImage(option, size).ifPresent(container.getChildren()::add);
            container.getChildren().add(keyRegion);
            displayLabels.forEach(container.getChildren()::add);

            last = option;
        } else {
            setGraphic(null);
        }
    }


    private Optional<ImageView> loadImage(AutocompletionOption<?> option, double size) {
        var icon = option.candidate().icon();
        if (icon == null) return Optional.empty();
        return icon.getImage().map(it -> {
            var view = new NearestImageView(it);
            view.setFitWidth(size * 1.2);
            view.setFitHeight(size * 1.2);
            return view;
        });
    }

    private void populateKeyHbox(String key, StringSearch.Result result, String labelStyle) {
        keyRegion.getChildren().clear();
        var list = new LinkedList<Pair<String, Boolean>>();

        int index = 0;
        for (var range : result.ranges()) {
            list.add(new Pair<>(key.substring(index, range.getKey()), false));
            list.add(new Pair<>(key.substring(range.getKey(), range.getKey() + range.getValue()), true));
            index = range.getKey() + range.getValue();
        }
        list.add(new Pair<>(key.substring(index), false));

        for (var slice : list) {
            if (slice.getKey().isEmpty()) continue;
            var label = new Label(slice.getKey());
            label.getStyleClass().add(SLICE_STYLE_CLASS);
            if (slice.getValue()) {
                label.getStyleClass().add(SLICE_MATCH_STYLE_CLASS);
            }
            label.setStyle(labelStyle);
            keyRegion.getChildren().add(label);
        }
    }
}

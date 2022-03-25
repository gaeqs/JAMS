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

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionOption;
import net.jamsimulator.jams.gui.image.nearest.NearestImageView;
import net.jamsimulator.jams.utils.StringSearch;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class AutocompletionPopupBasicViewElement extends HBox {

    private static final double DEFAULT_FONT_SIZE = 13.5;

    private final AutocompletionOption<?> option;

    private final HBox keyRegion = new HBox();
    private final LinkedList<Label> displayLabels = new LinkedList<>();

    public AutocompletionPopupBasicViewElement(AutocompletionOption<?> option, int maxKeyLength, List<Integer> maxNameLength, double zoom) {
        getStyleClass().add("autocompletion-popup-element");
        this.option = option;

        var labelStyle = "-fx-font-size: " + (DEFAULT_FONT_SIZE * zoom) + ";";

        int index = 0;
        for (var string : option.candidate().displayStrings()) {
            var label = new Label(StringUtils.addSpaces(string, maxNameLength.get(index++), true));
            label.setStyle(labelStyle);
            displayLabels.add(label);
        }

        populateKeyHbox(
                StringUtils.addSpaces(option.candidate().key(), maxKeyLength, true),
                option.searchResult(),
                labelStyle
        );

        loadImage().ifPresent(this.getChildren()::add);
        getChildren().add(keyRegion);
        displayLabels.forEach(getChildren()::add);
    }

    private Optional<ImageView> loadImage() {
        var icon = option.candidate().icon();
        if (icon == null) return Optional.empty();
        return icon.getImage().map(it -> {
            var view = new NearestImageView(it);
            view.setPreserveRatio(true);
            if (!displayLabels.isEmpty()) {
                view.fitHeightProperty().bind(displayLabels.getFirst().heightProperty());
            }
            return view;
        });
    }

    private void populateKeyHbox(String key, StringSearch.Result result, String labelStyle) {
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
            label.getStyleClass().add("autocompletion-popup-element-key-slice");
            if (slice.getValue()) {
                label.getStyleClass().add("autocompletion-popup-element-key-slice-match");
            }
            label.setStyle(labelStyle);
            keyRegion.getChildren().add(label);
        }
    }
}

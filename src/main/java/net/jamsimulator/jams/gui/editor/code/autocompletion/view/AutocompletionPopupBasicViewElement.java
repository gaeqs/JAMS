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
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionCandidate;
import net.jamsimulator.jams.gui.image.nearest.NearestImageView;

import java.util.Optional;

public class AutocompletionPopupBasicViewElement extends HBox {

    private static final double DEFAULT_FONT_SIZE = 12.0;

    private final AutocompletionCandidate<?> candidate;
    private final Label nameLabel;

    public AutocompletionPopupBasicViewElement(AutocompletionCandidate<?> candidate, double zoom) {
        this.candidate = candidate;
        this.nameLabel = new Label(candidate.key());

        nameLabel.setStyle("-fx-font-size: " + (DEFAULT_FONT_SIZE * zoom) + ";");

        loadImage().ifPresent(this.getChildren()::add);
        getChildren().add(nameLabel);
    }

    private Optional<ImageView> loadImage() {
        var icon = candidate.icon();
        if (icon == null) return Optional.empty();
        return icon.getImage().map(it -> {
            var view = new NearestImageView(it);
            view.setPreserveRatio(true);
            view.fitHeightProperty().bind(nameLabel.heightProperty());
            return view;
        });
    }

}

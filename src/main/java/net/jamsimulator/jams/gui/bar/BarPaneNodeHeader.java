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

package net.jamsimulator.jams.gui.bar;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

/**
 * Represents the header of a {@link BarPaneNode}. This header contains
 * information and options about the wrapped {@link javafx.scene.Node} of the {@link BarPaneNode}.
 * <p>
 * This header also allows to resize the {@link javafx.scene.Node}.
 *
 * @see BarPaneNode
 */
public class BarPaneNodeHeader extends AnchorPane {

    public static final int HEIGHT = 25;
    public static final Cursor CURSOR = Cursor.N_RESIZE;


    private final Button closeButton;

    private BarSnapshot snapshot;
    private Label label;
    private double relativeDragPosition;


    /**
     * Creates the header.
     *
     * @param barMap    the {@link BarMap}.
     * @param splitPane the {@link SplitPane} that handles the wrapped {@link javafx.scene.Node}, if present.
     */
    public BarPaneNodeHeader(BarMap barMap, SplitPane splitPane) {
        getStyleClass().add("bar-pane-node-header");
        setPrefHeight(HEIGHT);

        setCursor(CURSOR);

        closeButton = new Button("", new QualityImageView(Icons.BAR_CLOSE, 16, 16));
        closeButton.getStyleClass().add("bar-pane-node-header-button");
        closeButton.setOnAction(event -> barMap.searchButton(snapshot.getName()).ifPresent(BarButton::hide));
        closeButton.setCursor(Cursor.HAND);
        AnchorUtils.setAnchor(closeButton, 0, 0, -1, 1);
        getChildren().add(closeButton);

        if (splitPane != null) {
            registerFXEvents(splitPane);
        }
    }

    /**
     * Selected the given {@link BarSnapshot snapshot}.
     * This displays its information and allows this header to deselect if needed.
     *
     * @param snapshot the {@link BarSnapshot snapshot}.
     */
    public void selectSnapshot(BarSnapshot snapshot) {
        this.snapshot = snapshot;
        if (snapshot == null) {
            getChildren().clear();
        } else {

            label = snapshot.getLanguageNode()
                    .map(v -> (Label) new LanguageLabel(v))
                    .orElseGet(() -> new Label(snapshot.getName()));

            AnchorUtils.setAnchor(label, 0, 0, 5, -1);
            getChildren().setAll(label, closeButton);
        }

    }

    /**
     * Returns the {@link Label} that has the name of the header.
     *
     * @return the {@link Label}.
     */
    public Label getLabel() {
        return label;
    }

    private void registerFXEvents(SplitPane splitPane) {
        setOnMousePressed(event -> relativeDragPosition = event.getY());
        setOnMouseDragged(event -> {
            if (splitPane.getItems().size() < 2) return;
            double absolute = event.getSceneY();
            double min = splitPane.getLocalToSceneTransform().getTy();
            double max = min + splitPane.getHeight();
            double relative = (absolute - min - relativeDragPosition) / (max - min);
            splitPane.setDividerPosition(splitPane.getItems().size() - 2, relative);
        });
    }
}

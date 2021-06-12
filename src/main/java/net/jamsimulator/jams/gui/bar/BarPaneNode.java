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

import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.gui.util.AnchorUtils;

import java.util.Optional;

/**
 * Represents the section of a {@link BarPane} that manages one of its {@link BarSnapshot snapshot}.
 */
public class BarPaneNode extends AnchorPane {

    private final BarPaneNodeHeader header;
    private BarButton button;

    /**
     * Creates the node.
     *
     * @param map       the map of the {@link BarPane} holding this node.
     * @param splitPane the {@link SplitPane} used for the resizing event if required.
     */
    public BarPaneNode(BarMap map, SplitPane splitPane) {
        header = new BarPaneNodeHeader(map, splitPane);
        AnchorUtils.setAnchor(header, 0, -1, 0, 0);
    }

    /**
     * Returns the {@link BarButton button} of the {@link BarSnapshot snapshot} currently holding if present.
     *
     * @return the {@link BarButton button}.
     */
    public Optional<BarButton> getButton() {
        return Optional.ofNullable(button);
    }

    /**
     * Assigns the {@link BarSnapshot snapshot} of the given {@link BarButton button} to this node.
     * <p>
     * If a button is already selected, this will be hidden automatically.
     * <p>
     * This method DOES NOT enable the given button.
     *
     * @param button the {@link BarButton} button.
     */
    public void selectButton(BarButton button) {
        if (this.button != null) {
            this.button.forceHide();
        }

        this.button = button;
        if (button == null) {
            header.selectSnapshot(null);
            getChildren().clear();
        } else {
            header.selectSnapshot(button.getSnapshot());
            AnchorUtils.setAnchor(button.getSnapshot().getNode(), BarPaneNodeHeader.HEIGHT, 0, 0, 0);
            getChildren().setAll(header, button.getSnapshot().getNode());
        }
    }

}

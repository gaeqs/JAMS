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

package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ScrollPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.event.ActionRegisterEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnregisterEvent;
import net.jamsimulator.jams.gui.explorer.Explorer;

/**
 * Represents an explorer showing all actions registered in {@link JamsApplication#getActionManager()}.
 */
public class ActionsExplorer extends Explorer {

    private boolean smallRepresentation;

    /**
     * Creates an explorer.
     *
     * @param scrollPane the {@link ScrollPane} holding this explorer, if present.
     */
    public ActionsExplorer(ScrollPane scrollPane, boolean smallRepresentation) {
        super(scrollPane, false, true);
        this.smallRepresentation = smallRepresentation;
        generateResizeListeners();
    }

    @Override
    protected void generateMainSection() {
        mainSection = new ActionsExplorerMainSection(this);
        getChildren().add(mainSection);
        mainSection.expand();
        JamsApplication.getActionManager().registerListeners(this, true);
    }

    public boolean isSmallRepresentation() {
        return smallRepresentation;
    }

    public void setSmallRepresentation(boolean smallRepresentation) {
        if (this.smallRepresentation == smallRepresentation) return;
        this.smallRepresentation = smallRepresentation;
        ((ActionsExplorerMainSection) mainSection).setSmallRepresentation(smallRepresentation);
        super.refreshWidth();
    }

    @Listener
    private void onActionRegister(ActionRegisterEvent.After event) {
        ((ActionsExplorerMainSection) mainSection).addAction(event.getAction());
    }

    @Listener
    private void onActionUnregister(ActionUnregisterEvent.After event) {
        ((ActionsExplorerMainSection) mainSection).removeAction(event.getAction());
    }

    @Override
    public void refreshWidth() {
        super.refreshWidth();
        Platform.runLater(() -> checkRepresentationMode(getWidth()));
    }

    private void checkRepresentationMode(double width) {
        final int SCROLLBAR_ERROR = 25;
        double elementWidth = ((ActionsExplorerMainSection) mainSection).getBiggestElementInBigRepresentation();
        setSmallRepresentation(elementWidth + SCROLLBAR_ERROR >= width);
    }


    private void generateResizeListeners() {
        ChangeListener<? super Number> listener = (obs, old, val) -> checkRepresentationMode(val.doubleValue());
        if (scrollPane == null) {
            widthProperty().addListener(listener);
        } else {
            scrollPane.widthProperty().addListener(listener);
        }
    }
}

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

package net.jamsimulator.jams.gui.main;

import javafx.scene.Parent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnbindEvent;
import net.jamsimulator.jams.gui.theme.ThemedScene;

/**
 * Represents the main scene. This class listens both the {@link net.jamsimulator.jams.manager.ThemeManager} and
 * the {@link net.jamsimulator.jams.manager.ActionManager}.
 */
public class MainScene extends ThemedScene {

    public MainScene(Parent root) {
        super(root);
        JamsApplication.getActionManager().addAcceleratorsToScene(this, true);
    }

    public MainScene(Parent root, double width, double height) {
        super(root, width, height);
        JamsApplication.getActionManager().addAcceleratorsToScene(this, true);
    }

    public MainScene(Parent root, double width, double height, boolean depthBuffer) {
        super(root, width, height, depthBuffer);
        JamsApplication.getActionManager().addAcceleratorsToScene(this, true);
    }

    @Listener
    public void onActionBind(ActionBindEvent.After event) {
        JamsApplication.getActionManager().addAcceleratorsToScene(this, true);
    }

    @Listener
    public void onActionUnbind(ActionUnbindEvent.After event) {
        JamsApplication.getActionManager().addAcceleratorsToScene(this, true);
    }

}

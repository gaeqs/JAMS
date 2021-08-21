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

package net.jamsimulator.jams.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.event.ThemeRefreshEvent;

public class ThemedScene extends Scene {

    public ThemedScene(Parent root) {
        super(root, -1, -1, true, getSceneAntialiasing());
        initializeJamsListeners();
    }

    public ThemedScene(Parent root, double width, double height) {
        super(root, width, height, true, getSceneAntialiasing());
        initializeJamsListeners();
    }

    public ThemedScene(Parent root, double width, double height, boolean depthBuffer) {
        super(root, width, height, depthBuffer, getSceneAntialiasing());
        initializeJamsListeners();
    }

    private static SceneAntialiasing getSceneAntialiasing() {
        return (boolean) Jams.getMainConfiguration().get("appearance.antialiasing").orElse(false)
                ? SceneAntialiasing.BALANCED : SceneAntialiasing.DISABLED;
    }

    protected void initializeJamsListeners() {
        JamsApplication.getThemeManager().registerListeners(this, true);
        JamsApplication.getActionManager().registerListeners(this, true);
        //JamsApplication.getThemeManager().apply(this);
    }

    @Listener
    private void onThemeRefresh(ThemeRefreshEvent event) {
        //JamsApplication.getThemeManager().apply(this);
    }

}

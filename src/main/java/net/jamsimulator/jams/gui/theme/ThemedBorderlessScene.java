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

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.event.ThemeRefreshEvent;

public class ThemedBorderlessScene extends BorderlessScene {

    public ThemedBorderlessScene(Stage stage, Parent root) {
        super(stage, StageStyle.TRANSPARENT, root);
        initializeJamsListeners();
        setTransparentWindowStyle("-fx-background-color:rgb(0,0,0,0.2); -fx-border-color:transparent;");
        setSnapEnabled(System.getProperty("os.name").toLowerCase().contains("win"));
        removeDefaultCSS();
    }

    public ThemedBorderlessScene(Stage stage, Parent root, double width, double height) {
        super(stage, StageStyle.TRANSPARENT, root, width, height);
        initializeJamsListeners();
        setTransparentWindowStyle("-fx-background-color:rgb(0,0,0,0.2); -fx-border-color:transparent;");
        setSnapEnabled(System.getProperty("os.name").toLowerCase().contains("win"));
        removeDefaultCSS();
    }

    protected void initializeJamsListeners() {
        JamsApplication.getThemeManager().registerListeners(this, true);
        //JamsApplication.getThemeManager().apply(this);
    }

    @Listener
    private void onThemeRefresh(ThemeRefreshEvent event) {
        //  JamsApplication.getThemeManager().apply(this);
    }

}

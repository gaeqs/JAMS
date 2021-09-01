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

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.main.window.WindowButtonClose;
import net.jamsimulator.jams.gui.main.window.WindowButtonMaximize;
import net.jamsimulator.jams.gui.main.window.WindowButtonMinimize;
import net.jamsimulator.jams.gui.util.AnchorUtils;


public class TopBar extends AnchorPane {


    private final QualityImageView view;
    private final MainMenuBar menuBar;
    private final HBox windowButtons;

    private boolean transparentMode;

    public TopBar(Stage stage, boolean transparentMode) {
        getStyleClass().add("top-bar");
        this.transparentMode = transparentMode;
        view = new QualityImageView(Icons.LOGO);
        view.setFitWidth(20);
        view.setFitHeight(20);
        AnchorUtils.setAnchor(view, 5, 5, 5, -1);

        menuBar = new MainMenuBar();

        windowButtons = new HBox();
        windowButtons.getStyleClass().add("window-buttons");
        generateButtons(stage);
        AnchorUtils.setAnchor(windowButtons, 0, 0, -1, 0);


        refresh();
    }

    public QualityImageView getView() {
        return view;
    }

    public MainMenuBar getMenuBar() {
        return menuBar;
    }

    public HBox getWindowButtons() {
        return windowButtons;
    }

    private void generateButtons(Stage stage) {
        var minimize = new WindowButtonMinimize(stage);
        var maximize = new WindowButtonMaximize(stage);
        var close = new WindowButtonClose(stage);
        windowButtons.getChildren().addAll(minimize, maximize, close);
    }

    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        if (transparentMode == this.transparentMode) return;
        this.transparentMode = transparentMode;
        refresh();
    }

    private void refresh() {
        getChildren().clear();
        AnchorUtils.setAnchor(menuBar, 0, 0, transparentMode ? 25 : 0, transparentMode ? 100 : 0);

        if (transparentMode) {
            getChildren().addAll(view, menuBar, windowButtons);
        } else {
            getChildren().add(menuBar);
        }
    }
}

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

package net.jamsimulator.jams.gui.main.window;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.stage.Stage;
import net.jamsimulator.jams.gui.image.icon.IconData;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;

public class WindowButton extends Button {

    public static final float FIT = 12.0f;
    public static final String STYLE_CLASS = "window-button";

    protected final Stage stage;
    protected final QualityImageView imageView;

    public WindowButton(Stage stage, IconData display) {
        super("", new QualityImageView(display, FIT, FIT));
        getStyleClass().add(STYLE_CLASS);
        setAlignment(Pos.CENTER);
        this.stage = stage;
        this.imageView = (QualityImageView) getGraphic();

        imageView.iconProperty().addListener((obs, old, val) -> Platform.runLater(() -> {
            imageView.setFitWidth(FIT);
            imageView.setFitHeight(FIT);
        }));

        var image = (QualityImageView) getGraphic();
        var blend = new Blend();
        blend.setMode(BlendMode.SRC_ATOP);
        blend.setTopInput(new ColorInput(0, 0, 10000, 10000, getTextFill()));
        image.setEffect(blend);

        textFillProperty().addListener((obs, old, val) -> {
            blend.setTopInput(new ColorInput(0, 0, 10000, 10000, val));
        });

    }

    public Stage getStage() {
        return stage;
    }
}

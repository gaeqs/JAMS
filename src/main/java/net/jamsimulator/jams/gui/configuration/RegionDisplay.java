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

package net.jamsimulator.jams.gui.configuration;

import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

public class RegionDisplay extends HBox {

    public static final String STYLE_CLASS = "region-display";

    public RegionDisplay(String languageNode, String region) {
        getStyleClass().add(STYLE_CLASS);
        if (languageNode != null) {
            languageNode += "_REGION_" + region.toUpperCase();
        }
        refresh(languageNode);
    }

    public RegionDisplay(String languageNode) {
        getStyleClass().add(STYLE_CLASS);
        refresh(languageNode);
    }


    private void refresh(String languageNode) {
        getChildren().clear();

        Label label = new LanguageLabel(languageNode);
        Separator separator = new Separator(Orientation.HORIZONTAL);

        separator.prefWidthProperty().bind(widthProperty().subtract(label.widthProperty()).subtract(20));

        getChildren().add(new Group(label));
        getChildren().add(separator);
    }

}

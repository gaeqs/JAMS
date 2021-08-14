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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * Small helper class that fills the space between two {@link Bar}s.
 */
public class FillRegion extends Region implements ChangeListener<Number> {

    private final Orientation orientation;
    private final Pane holder, first, second;

    public FillRegion(BarPosition position, Pane holder, Bar first, Bar second) {
        getStyleClass().addAll("bar");
        position.addStyleClasses(getStyleClass());
        this.orientation = position.getOrientation();
        this.holder = holder;
        this.first = first.getNode();
        this.second = second.getNode();


        if (orientation == Orientation.VERTICAL) {
            holder.heightProperty().addListener(this);
            this.first.heightProperty().addListener(this);
            this.second.heightProperty().addListener(this);
        } else {
            holder.widthProperty().addListener(this);
            this.first.widthProperty().addListener(this);
            this.second.widthProperty().addListener(this);
        }
    }

    @Override
    public void changed(ObservableValue<? extends Number> obs, Number old, Number val) {
        if (orientation == Orientation.VERTICAL) {
            Platform.runLater(() -> setPrefHeight(Math.max(holder.getHeight() - first.getHeight() - second.getHeight(), 0)));
        } else {
            Platform.runLater(() -> setPrefWidth(Math.max(holder.getWidth() - first.getWidth() - second.getWidth(), 0)));
        }
    }
}

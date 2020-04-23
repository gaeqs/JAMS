/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.sidebar;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SidebarFillRegion extends Region implements ChangeListener<Number> {

	private VBox holder, top, bottom;

	public SidebarFillRegion(boolean left, VBox holder, Sidebar top, Sidebar bottom) {
		getStyleClass().addAll("sidebar", left ? "sidebar-left" : "sidebar-right");
		this.holder = holder;
		this.top = top;
		this.bottom = bottom;

		holder.heightProperty().addListener(this);
		top.heightProperty().addListener(this);
		bottom.heightProperty().addListener(this);
	}

	@Override
	public void changed(ObservableValue<? extends Number> obs, Number old, Number val) {
		Platform.runLater(() -> setPrefHeight(Math.max(holder.getHeight() - top.getHeight() - bottom.getHeight(), 0)));
	}
}

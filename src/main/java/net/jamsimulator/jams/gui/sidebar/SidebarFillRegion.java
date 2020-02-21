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

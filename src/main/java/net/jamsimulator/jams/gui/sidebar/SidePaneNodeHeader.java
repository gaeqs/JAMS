package net.jamsimulator.jams.gui.sidebar;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.sidebar.event.SidebarChangeNodeEvent;
import net.jamsimulator.jams.utils.AnchorUtils;


public class SidePaneNodeHeader extends AnchorPane {

	public static final int HEIGHT = 25;
	public static final Cursor TOP_NULL_CURSOR = Cursor.DEFAULT;
	public static final Cursor TOP_NOT_NULL_CURSOR = Cursor.N_RESIZE;

	private SidePane sidePane;

	private boolean top;

	private String name;
	private Label label;

	private double relativeDragPosition;

	public SidePaneNodeHeader(SidePane sidePane, String name, boolean top) {
		this.sidePane = sidePane;
		this.name = name;

		this.top = top;

		getStyleClass().add("side-pane-node-header");
		setPrefHeight(HEIGHT);

		if (!top) {
			setCursor(sidePane.getTop() == null ? TOP_NULL_CURSOR : TOP_NOT_NULL_CURSOR);
		}

		label = new Label(name);
		AnchorUtils.setAnchor(label, 0, 0, 5, -1);
		getChildren().add(label);

		sidePane.registerListeners(this);
		registerFXEvents();
	}

	public String getName() {
		return name;
	}

	public Label getLabel() {
		return label;
	}

	public boolean isTop() {
		return top;
	}

	@Listener
	private void onTopChange(SidebarChangeNodeEvent event) {
		if (top || !event.isTop()) return;

		if (event.getFrom() == null && event.getTo() != null) {
			setCursor(TOP_NOT_NULL_CURSOR);
		} else if (event.getFrom() != null && event.getTo() == null) {
			setCursor(TOP_NULL_CURSOR);
		}
	}

	private void registerFXEvents() {
		if(top) return;
		setOnMousePressed(event -> relativeDragPosition = event.getY());
		setOnMouseDragged(event -> {
			if (getCursor() == TOP_NULL_CURSOR) return;
			double absolute = event.getSceneY();
			double min = sidePane.getLocalToSceneTransform().getTy();
			double max = min + sidePane.getHeight();
			double relative = (absolute - min - relativeDragPosition) / (max - min);
			sidePane.setDividerPosition(0, relative);
		});
	}
}

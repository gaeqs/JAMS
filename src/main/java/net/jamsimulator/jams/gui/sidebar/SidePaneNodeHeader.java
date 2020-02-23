package net.jamsimulator.jams.gui.sidebar;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.sidebar.event.SidebarChangeNodeEvent;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * Represents the header of a {@link SidePaneNode}. This header contains
 * information and options about the wrapped {@link javafx.scene.Node} of the {@link SidePaneNode}.
 * <p>
 * This header also allows to resize the {@link javafx.scene.Node}.
 *
 * @see SidePaneNode
 */
public class SidePaneNodeHeader extends AnchorPane {

	public static final int HEIGHT = 25;
	public static final Cursor TOP_NULL_CURSOR = Cursor.DEFAULT;
	public static final Cursor TOP_NOT_NULL_CURSOR = Cursor.N_RESIZE;

	private SidePane sidePane;

	private boolean top;

	private String name;
	private Label label;

	private double relativeDragPosition;

	/**
	 * Creates the header.
	 *
	 * @param sidePane the {@link SidePane} that handles the wrapped {@link javafx.scene.Node}.
	 * @param name     the name of the {@link javafx.scene.Node}.
	 * @param top      whether the {@link Sidebar} containing the {@link javafx.scene.Node} is a top {@link Sidebar}.
	 */
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

	/**
	 * Returns the name of the header.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the {@link Label} that has the name of the header.
	 *
	 * @return the {@link Label}.
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * Returns whether the {@link Sidebar} containing the {@link javafx.scene.Node} is a top {@link Sidebar}.
	 *
	 * @return whether the {@link Sidebar} containing the {@link javafx.scene.Node} is a top {@link Sidebar}.
	 */
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
		if (top) return;
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
package net.jamsimulator.jams.gui.bottombar;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.utils.AnchorUtils;

/**
 * Represents the header of a {@link BottomPaneNode}. This header contains
 * information and options about the wrapped {@link javafx.scene.Node} of the {@link BottomPaneNode}.
 * <p>
 * This header also allows to resize the {@link javafx.scene.Node}.
 *
 * @see BottomPaneNode
 */
public class BottomPaneNodeHeader extends AnchorPane {

	public static final int HEIGHT = 25;
	public static final Cursor CURSOR = Cursor.N_RESIZE;

	private SplitPane verticalSplitPane;

	private String name;
	private Label label;

	private double relativeDragPosition;


	/**
	 * Creates the header.
	 *
	 * @param verticalSplitPane the {@link SplitPane} that handles the wrapped {@link javafx.scene.Node}.
	 * @param name              the name of the {@link javafx.scene.Node}.
	 */
	public BottomPaneNodeHeader(SplitPane verticalSplitPane, String name) {
		this.verticalSplitPane = verticalSplitPane;
		this.name = name;

		getStyleClass().add("bottom-pane-node-header");
		setPrefHeight(HEIGHT);

		setCursor(CURSOR);

		label = new Label(name);
		AnchorUtils.setAnchor(label, 0, 0, 5, -1);
		getChildren().add(label);
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

	private void registerFXEvents() {
		setOnMousePressed(event -> relativeDragPosition = event.getY());
		setOnMouseDragged(event -> {
			double absolute = event.getSceneY();
			double min = verticalSplitPane.getLocalToSceneTransform().getTy();
			double max = min + verticalSplitPane.getHeight();
			double relative = (absolute - min - relativeDragPosition) / (max - min);
			verticalSplitPane.setDividerPosition(0, relative);
		});
	}
}

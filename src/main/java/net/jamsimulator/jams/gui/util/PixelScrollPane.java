package net.jamsimulator.jams.gui.util;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

/**
 * Represents a {@link ScrollPane} that scrolls the same amount of pixels each time,
 * independently the height/width of the content.
 */
public class PixelScrollPane extends ScrollPane {

	private final PixelScrollPaneSkin skin;

	/**
	 * Creates the scroll pane.
	 */
	public PixelScrollPane() {
		setSkin(skin = new PixelScrollPaneSkin(this));
	}

	/**
	 * Creates the scroll pane.
	 *
	 * @param increment the amount of pixels to scroll each time.
	 */
	public PixelScrollPane(double increment) {
		setSkin(skin = new PixelScrollPaneSkin(this, increment));
	}

	/**
	 * Creates the scroll pane.
	 *
	 * @param node the node inside this scroll pane.
	 */
	public PixelScrollPane(Node node) {
		super(node);
		setSkin(skin = new PixelScrollPaneSkin(this));
	}

	/**
	 * Creates the scroll pane.
	 *
	 * @param node      the node inside this scroll pane.
	 * @param increment the amount of pixels to scroll each time.
	 */
	public PixelScrollPane(Node node, double increment) {
		super(node);
		setSkin(skin = new PixelScrollPaneSkin(this, increment));
	}

	/**
	 * Returns the amount of pixels to scroll each time.
	 *
	 * @return the amount of pixels.
	 */
	public double getIncrement() {
		return skin.getIncrement();
	}

	/**
	 * Sets the amount of pixels to scroll each time.
	 *
	 * @param increment the amount of pixels.
	 */
	public void setIncrement(double increment) {
		skin.setIncrement(increment);
	}
}

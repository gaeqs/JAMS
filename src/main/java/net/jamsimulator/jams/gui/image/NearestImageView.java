package net.jamsimulator.jams.gui.image;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents an {@link ImageView} that doesn't smooth the image inside..
 */
public class NearestImageView extends ImageView {

	/**
	 * Creates the nearest image view.
	 */
	public NearestImageView() {
		setSmooth(false);
	}

	/**
	 * Creates the nearest image view.
	 *
	 * @param url the image's URL.
	 */
	public NearestImageView(String url) {
		super(url);
		setSmooth(false);
	}

	/**
	 * Creates the nearest image view.
	 *
	 * @param image the image.
	 */
	public NearestImageView(Image image) {
		super(image);
		setSmooth(false);
	}

}

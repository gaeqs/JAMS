package net.jamsimulator.jams.gui.explorer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.JamsApplication;

import java.io.File;

/**
 * Represents a file inside an {@link Explorer}.
 */
public class ExplorerFile extends HBox {

	public static final int SPACING = 5;
	private File file;

	//REPRESENTATION DATA
	private ImageView icon;
	private Label label;

	/**
	 * Creates an explorer file.
	 *
	 * @param file the file to represent.
	 */
	public ExplorerFile(File file) {
		this.file = file;
		loadElements();
	}

	/**
	 * Returns the {@link File} represented by this explorer file.
	 *
	 * @return the {@link File}.
	 */
	public File getFile() {
		return file;
	}


	private void loadElements() {
		icon = new ImageView(JamsApplication.getFileIconManager().getImageByFile(file));
		label = new Label(file.getName());

		getChildren().addAll(icon, label);
		setSpacing(SPACING);
		setAlignment(Pos.CENTER_LEFT);
	}
}
